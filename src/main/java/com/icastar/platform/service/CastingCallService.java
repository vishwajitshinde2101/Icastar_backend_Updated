package com.icastar.platform.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.icastar.platform.dto.castingcall.CastingCallResponseDto;
import com.icastar.platform.dto.castingcall.CreateCastingCallDto;
import com.icastar.platform.dto.castingcall.UpdateCastingCallDto;
import com.icastar.platform.entity.CastingCall;
import com.icastar.platform.entity.User;
import com.icastar.platform.repository.CastingCallRepository;
import com.icastar.platform.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CastingCallService {

    private final CastingCallRepository castingCallRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    // 1. List all casting calls with filtering
    @Transactional(readOnly = true)
    public Page<CastingCallResponseDto> getAllCastingCalls(
            User recruiter,
            String searchTerm,
            CastingCall.CastingCallStatus status,
            String roleType,
            String projectType,
            String location,
            Boolean isUrgent,
            Boolean isFeatured,
            Pageable pageable) {

        // SECURITY: Validate recruiter role
        validateRecruiterRole(recruiter);

        log.info("Fetching casting calls for recruiter: {}", recruiter.getEmail());

        Page<CastingCall> castingCalls = castingCallRepository.findCastingCallsWithFilters(
            searchTerm, status, roleType, projectType, location, isUrgent, isFeatured,
            null, null, pageable
        );

        // Filter by recruiter ownership
        List<CastingCall> filteredList = castingCalls.getContent().stream()
            .filter(cc -> cc.getRecruiter().getId().equals(recruiter.getId()))
            .collect(Collectors.toList());

        return new PageImpl<>(
            filteredList.stream().map(this::convertToResponseDto).collect(Collectors.toList()),
            pageable,
            filteredList.size()
        );
    }

    // 2. Get single casting call
    @Transactional(readOnly = true)
    public CastingCallResponseDto getCastingCallById(User recruiter, Long castingCallId) {
        validateRecruiterRole(recruiter);

        CastingCall castingCall = castingCallRepository.findById(castingCallId)
            .orElseThrow(() -> new RuntimeException("Casting call not found"));

        // SECURITY: Validate ownership
        validateOwnership(castingCall, recruiter);

        return convertToResponseDto(castingCall);
    }

    // 3. Create casting call
    public CastingCallResponseDto createCastingCall(User recruiter, CreateCastingCallDto dto) {
        validateRecruiterRole(recruiter);

        log.info("Creating casting call '{}' for recruiter: {}", dto.getTitle(), recruiter.getEmail());

        CastingCall castingCall = new CastingCall();
        castingCall.setRecruiter(recruiter);
        mapDtoToEntity(dto, castingCall);

        // Set initial status
        castingCall.setStatus(CastingCall.CastingCallStatus.DRAFT);
        castingCall.setApplicationsCount(0);
        castingCall.setViewsCount(0);
        castingCall.setShortlistedCount(0);
        castingCall.setSelectedCount(0);

        CastingCall saved = castingCallRepository.save(castingCall);
        log.info("Created casting call ID: {}", saved.getId());

        return convertToResponseDto(saved);
    }

    // 4. Update casting call
    public CastingCallResponseDto updateCastingCall(User recruiter, Long castingCallId, UpdateCastingCallDto dto) {
        validateRecruiterRole(recruiter);

        CastingCall castingCall = castingCallRepository.findById(castingCallId)
            .orElseThrow(() -> new RuntimeException("Casting call not found"));

        // SECURITY: Validate ownership
        validateOwnership(castingCall, recruiter);

        log.info("Updating casting call ID: {} by recruiter: {}", castingCallId, recruiter.getEmail());

        // Only allow updates if status is DRAFT or OPEN
        if (castingCall.getStatus() == CastingCall.CastingCallStatus.CLOSED ||
            castingCall.getStatus() == CastingCall.CastingCallStatus.CANCELLED) {
            throw new RuntimeException("Cannot update closed or cancelled casting call");
        }

        // Apply partial updates (only non-null fields)
        applyPartialUpdate(dto, castingCall);

        CastingCall updated = castingCallRepository.save(castingCall);
        log.info("Updated casting call ID: {}", castingCallId);

        return convertToResponseDto(updated);
    }

    // 5. Delete casting call (soft delete, only if DRAFT)
    public Map<String, Object> deleteCastingCall(User recruiter, Long castingCallId) {
        validateRecruiterRole(recruiter);

        CastingCall castingCall = castingCallRepository.findById(castingCallId)
            .orElseThrow(() -> new RuntimeException("Casting call not found"));

        // SECURITY: Validate ownership
        validateOwnership(castingCall, recruiter);

        // Only allow deletion if DRAFT
        if (castingCall.getStatus() != CastingCall.CastingCallStatus.DRAFT) {
            throw new RuntimeException("Can only delete casting calls in DRAFT status. Use close instead.");
        }

        log.info("Deleting casting call ID: {} by recruiter: {}", castingCallId, recruiter.getEmail());

        // Soft delete
        castingCall.setDeletedAt(LocalDateTime.now());
        castingCall.setStatus(CastingCall.CastingCallStatus.CANCELLED);
        castingCallRepository.save(castingCall);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Casting call deleted successfully");
        response.put("castingCallId", castingCallId);
        return response;
    }

    // 6. Publish casting call
    public CastingCallResponseDto publishCastingCall(User recruiter, Long castingCallId) {
        validateRecruiterRole(recruiter);

        CastingCall castingCall = castingCallRepository.findById(castingCallId)
            .orElseThrow(() -> new RuntimeException("Casting call not found"));

        // SECURITY: Validate ownership
        validateOwnership(castingCall, recruiter);

        log.info("Publishing casting call ID: {} by recruiter: {}", castingCallId, recruiter.getEmail());

        // Validate status transition
        if (castingCall.getStatus() != CastingCall.CastingCallStatus.DRAFT) {
            throw new RuntimeException("Can only publish casting calls in DRAFT status");
        }

        // Validate required fields
        validateForPublish(castingCall);

        castingCall.setStatus(CastingCall.CastingCallStatus.OPEN);
        castingCall.setPublishedAt(LocalDateTime.now());

        CastingCall published = castingCallRepository.save(castingCall);
        log.info("Published casting call ID: {}", castingCallId);

        return convertToResponseDto(published);
    }

    // 7. Close casting call
    public CastingCallResponseDto closeCastingCall(User recruiter, Long castingCallId) {
        validateRecruiterRole(recruiter);

        CastingCall castingCall = castingCallRepository.findById(castingCallId)
            .orElseThrow(() -> new RuntimeException("Casting call not found"));

        // SECURITY: Validate ownership
        validateOwnership(castingCall, recruiter);

        log.info("Closing casting call ID: {} by recruiter: {}", castingCallId, recruiter.getEmail());

        // Validate status transition
        if (castingCall.getStatus() != CastingCall.CastingCallStatus.OPEN) {
            throw new RuntimeException("Can only close casting calls in OPEN status");
        }

        castingCall.setStatus(CastingCall.CastingCallStatus.CLOSED);
        castingCall.setClosedAt(LocalDateTime.now());

        CastingCall closed = castingCallRepository.save(castingCall);
        log.info("Closed casting call ID: {}", castingCallId);

        return convertToResponseDto(closed);
    }

    // Security validators
    private void validateRecruiterRole(User user) {
        if (!User.UserRole.RECRUITER.equals(user.getRole())) {
            log.warn("Access denied for non-recruiter user: {} (role: {})", user.getEmail(), user.getRole());
            throw new RuntimeException("Access forbidden: Recruiter role required");
        }
    }

    private void validateOwnership(CastingCall castingCall, User recruiter) {
        if (!castingCall.getRecruiter().getId().equals(recruiter.getId())) {
            log.warn("Unauthorized access attempt by recruiter {} to casting call {} owned by {}",
                    recruiter.getId(), castingCall.getId(), castingCall.getRecruiter().getId());
            throw new RuntimeException("You don't have permission to access this casting call");
        }
    }

    private void validateForPublish(CastingCall castingCall) {
        List<String> errors = new ArrayList<>();

        if (castingCall.getTitle() == null || castingCall.getTitle().isBlank()) {
            errors.add("Title is required");
        }
        if (castingCall.getDescription() == null || castingCall.getDescription().isBlank()) {
            errors.add("Description is required");
        }
        if (castingCall.getAuditionDeadline() == null) {
            errors.add("Audition deadline is required");
        }

        if (!errors.isEmpty()) {
            throw new RuntimeException("Cannot publish: " + String.join(", ", errors));
        }
    }

    // Conversion helpers
    private CastingCallResponseDto convertToResponseDto(CastingCall entity) {
        String recruiterName = entity.getRecruiter().getFirstName() + " " + entity.getRecruiter().getLastName();

        return CastingCallResponseDto.builder()
            .id(entity.getId())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .requirements(entity.getRequirements())
            .location(entity.getLocation())
            .roleType(entity.getRoleType())
            .characterName(entity.getCharacterName())
            .projectName(entity.getProjectName())
            .projectType(entity.getProjectType())
            .status(entity.getStatus())
            .publishedAt(entity.getPublishedAt())
            .closedAt(entity.getClosedAt())
            .compensationMin(entity.getCompensationMin())
            .compensationMax(entity.getCompensationMax())
            .currency(entity.getCurrency())
            .isPaid(entity.getIsPaid())
            .paymentTerms(entity.getPaymentTerms())
            .auditionDate(entity.getAuditionDate())
            .auditionDeadline(entity.getAuditionDeadline())
            .estimatedShootingStart(entity.getEstimatedShootingStart())
            .estimatedShootingEnd(entity.getEstimatedShootingEnd())
            .shootingDurationDays(entity.getShootingDurationDays())
            .ageRangeMin(entity.getAgeRangeMin())
            .ageRangeMax(entity.getAgeRangeMax())
            .genderPreference(entity.getGenderPreference())
            .requiredSkills(entity.getRequiredSkills())
            .preferredLanguages(entity.getPreferredLanguages())
            .physicalRequirements(entity.getPhysicalRequirements())
            .isUrgent(entity.getIsUrgent())
            .isFeatured(entity.getIsFeatured())
            .acceptsRemoteAuditions(entity.getAcceptsRemoteAuditions())
            .requiresVideoAudition(entity.getRequiresVideoAudition())
            .auditionFormat(entity.getAuditionFormat())
            .auditionLocation(entity.getAuditionLocation())
            .contactEmail(entity.getContactEmail())
            .contactPhone(entity.getContactPhone())
            .additionalNotes(entity.getAdditionalNotes())
            .applicationsCount(entity.getApplicationsCount())
            .viewsCount(entity.getViewsCount())
            .shortlistedCount(entity.getShortlistedCount())
            .selectedCount(entity.getSelectedCount())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .recruiterId(entity.getRecruiter().getId())
            .recruiterName(recruiterName)
            .recruiterCompany("Company") // TODO: Add company field or fetch from RecruiterProfile
            .build();
    }

    private void mapDtoToEntity(CreateCastingCallDto dto, CastingCall entity) {
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setRequirements(dto.getRequirements());
        entity.setLocation(dto.getLocation());
        entity.setRoleType(dto.getRoleType());
        entity.setCharacterName(dto.getCharacterName());
        entity.setProjectName(dto.getProjectName());
        entity.setProjectType(dto.getProjectType());

        entity.setCompensationMin(dto.getCompensationMin());
        entity.setCompensationMax(dto.getCompensationMax());
        entity.setCurrency(dto.getCurrency());
        entity.setIsPaid(dto.getIsPaid());
        entity.setPaymentTerms(dto.getPaymentTerms());

        entity.setAuditionDate(dto.getAuditionDate());
        entity.setAuditionDeadline(dto.getAuditionDeadline());
        entity.setEstimatedShootingStart(dto.getEstimatedShootingStart());
        entity.setEstimatedShootingEnd(dto.getEstimatedShootingEnd());
        entity.setShootingDurationDays(dto.getShootingDurationDays());

        entity.setAgeRangeMin(dto.getAgeRangeMin());
        entity.setAgeRangeMax(dto.getAgeRangeMax());
        entity.setGenderPreference(dto.getGenderPreference());
        entity.setRequiredSkills(dto.getRequiredSkills());
        entity.setPreferredLanguages(dto.getPreferredLanguages());
        entity.setPhysicalRequirements(dto.getPhysicalRequirements());

        entity.setIsUrgent(dto.getIsUrgent());
        entity.setIsFeatured(dto.getIsFeatured());
        entity.setAcceptsRemoteAuditions(dto.getAcceptsRemoteAuditions());
        entity.setRequiresVideoAudition(dto.getRequiresVideoAudition());

        entity.setAuditionFormat(dto.getAuditionFormat());
        entity.setAuditionLocation(dto.getAuditionLocation());
        entity.setContactEmail(dto.getContactEmail());
        entity.setContactPhone(dto.getContactPhone());
        entity.setAdditionalNotes(dto.getAdditionalNotes());
    }

    private void applyPartialUpdate(UpdateCastingCallDto dto, CastingCall entity) {
        if (dto.getTitle() != null) entity.setTitle(dto.getTitle());
        if (dto.getDescription() != null) entity.setDescription(dto.getDescription());
        if (dto.getRequirements() != null) entity.setRequirements(dto.getRequirements());
        if (dto.getLocation() != null) entity.setLocation(dto.getLocation());
        if (dto.getRoleType() != null) entity.setRoleType(dto.getRoleType());
        if (dto.getCharacterName() != null) entity.setCharacterName(dto.getCharacterName());
        if (dto.getProjectName() != null) entity.setProjectName(dto.getProjectName());
        if (dto.getProjectType() != null) entity.setProjectType(dto.getProjectType());

        if (dto.getCompensationMin() != null) entity.setCompensationMin(dto.getCompensationMin());
        if (dto.getCompensationMax() != null) entity.setCompensationMax(dto.getCompensationMax());
        if (dto.getCurrency() != null) entity.setCurrency(dto.getCurrency());
        if (dto.getIsPaid() != null) entity.setIsPaid(dto.getIsPaid());
        if (dto.getPaymentTerms() != null) entity.setPaymentTerms(dto.getPaymentTerms());

        if (dto.getAuditionDate() != null) entity.setAuditionDate(dto.getAuditionDate());
        if (dto.getAuditionDeadline() != null) entity.setAuditionDeadline(dto.getAuditionDeadline());
        if (dto.getEstimatedShootingStart() != null) entity.setEstimatedShootingStart(dto.getEstimatedShootingStart());
        if (dto.getEstimatedShootingEnd() != null) entity.setEstimatedShootingEnd(dto.getEstimatedShootingEnd());
        if (dto.getShootingDurationDays() != null) entity.setShootingDurationDays(dto.getShootingDurationDays());

        if (dto.getAgeRangeMin() != null) entity.setAgeRangeMin(dto.getAgeRangeMin());
        if (dto.getAgeRangeMax() != null) entity.setAgeRangeMax(dto.getAgeRangeMax());
        if (dto.getGenderPreference() != null) entity.setGenderPreference(dto.getGenderPreference());
        if (dto.getRequiredSkills() != null) entity.setRequiredSkills(dto.getRequiredSkills());
        if (dto.getPreferredLanguages() != null) entity.setPreferredLanguages(dto.getPreferredLanguages());
        if (dto.getPhysicalRequirements() != null) entity.setPhysicalRequirements(dto.getPhysicalRequirements());

        if (dto.getIsUrgent() != null) entity.setIsUrgent(dto.getIsUrgent());
        if (dto.getIsFeatured() != null) entity.setIsFeatured(dto.getIsFeatured());
        if (dto.getAcceptsRemoteAuditions() != null) entity.setAcceptsRemoteAuditions(dto.getAcceptsRemoteAuditions());
        if (dto.getRequiresVideoAudition() != null) entity.setRequiresVideoAudition(dto.getRequiresVideoAudition());

        if (dto.getAuditionFormat() != null) entity.setAuditionFormat(dto.getAuditionFormat());
        if (dto.getAuditionLocation() != null) entity.setAuditionLocation(dto.getAuditionLocation());
        if (dto.getContactEmail() != null) entity.setContactEmail(dto.getContactEmail());
        if (dto.getContactPhone() != null) entity.setContactPhone(dto.getContactPhone());
        if (dto.getAdditionalNotes() != null) entity.setAdditionalNotes(dto.getAdditionalNotes());
    }
}
