package com.icastar.platform.service;

import com.icastar.platform.dto.castingcall.*;
import com.icastar.platform.entity.CastingCall;
import com.icastar.platform.entity.CastingCallApplication;
import com.icastar.platform.entity.User;
import com.icastar.platform.repository.CastingCallApplicationRepository;
import com.icastar.platform.repository.CastingCallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CastingCallApplicationService {

    private final CastingCallRepository castingCallRepository;
    private final CastingCallApplicationRepository applicationRepository;

    // 8. List applications for a casting call
    @Transactional(readOnly = true)
    public Page<CastingCallApplicationResponseDto> getApplications(
            User recruiter,
            Long castingCallId,
            CastingCallApplication.ApplicationStatus status,
            Boolean isShortlisted,
            Integer minRating,
            Pageable pageable) {

        validateRecruiterRole(recruiter);

        CastingCall castingCall = castingCallRepository.findById(castingCallId)
            .orElseThrow(() -> new RuntimeException("Casting call not found"));

        // SECURITY: Validate ownership
        validateOwnership(castingCall, recruiter);

        log.info("Fetching applications for casting call ID: {} with filters", castingCallId);

        Page<CastingCallApplication> applications = applicationRepository.findWithFilters(
            castingCallId, status, isShortlisted, minRating, pageable
        );

        return applications.map(this::convertToResponseDto);
    }

    // 9. Get single application
    @Transactional(readOnly = true)
    public CastingCallApplicationResponseDto getApplicationById(
            User recruiter,
            Long castingCallId,
            Long applicationId) {

        validateRecruiterRole(recruiter);

        CastingCall castingCall = castingCallRepository.findById(castingCallId)
            .orElseThrow(() -> new RuntimeException("Casting call not found"));

        validateOwnership(castingCall, recruiter);

        CastingCallApplication application = applicationRepository
            .findByCastingCallIdAndId(castingCallId, applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        return convertToResponseDto(application);
    }

    // 10. Update application status
    public CastingCallApplicationResponseDto updateApplicationStatus(
            User recruiter,
            Long castingCallId,
            Long applicationId,
            UpdateApplicationStatusDto dto) {

        validateRecruiterRole(recruiter);

        CastingCall castingCall = castingCallRepository.findById(castingCallId)
            .orElseThrow(() -> new RuntimeException("Casting call not found"));

        validateOwnership(castingCall, recruiter);

        CastingCallApplication application = applicationRepository
            .findByCastingCallIdAndId(castingCallId, applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        log.info("Updating application ID: {} status to {} by recruiter: {}",
                 applicationId, dto.getStatus(), recruiter.getEmail());

        // Validate and apply status transition
        CastingCallApplication.ApplicationStatus oldStatus = application.getStatus();
        validateApplicationStatusTransition(oldStatus, dto.getStatus());

        application.setStatus(dto.getStatus());

        // Set timestamps based on status
        LocalDateTime now = LocalDateTime.now();
        switch (dto.getStatus()) {
            case UNDER_REVIEW:
                if (application.getReviewedAt() == null) {
                    application.setReviewedAt(now);
                }
                break;
            case SHORTLISTED:
                application.setIsShortlisted(true);
                if (application.getShortlistedAt() == null) {
                    application.setShortlistedAt(now);
                    incrementCastingCallCounter(castingCall, "shortlisted");
                }
                break;
            case CALLBACK_SCHEDULED:
                if (dto.getCallbackDate() != null) {
                    application.setCallbackDate(dto.getCallbackDate());
                    application.setCallbackLocation(dto.getCallbackLocation());
                    application.setCallbackNotes(dto.getCallbackNotes());
                }
                if (application.getCallbackScheduledAt() == null) {
                    application.setCallbackScheduledAt(now);
                }
                break;
            case CALLBACK_COMPLETED:
                if (application.getCallbackCompletedAt() == null) {
                    application.setCallbackCompletedAt(now);
                }
                break;
            case SELECTED:
                application.setIsSelected(true);
                if (application.getSelectedAt() == null) {
                    application.setSelectedAt(now);
                    incrementCastingCallCounter(castingCall, "selected");
                }
                break;
            case REJECTED:
                if (application.getRejectedAt() == null) {
                    application.setRejectedAt(now);
                }
                if (dto.getRejectionReason() != null) {
                    application.setRejectionReason(dto.getRejectionReason());
                }
                break;
            case WITHDRAWN:
                if (application.getWithdrawnAt() == null) {
                    application.setWithdrawnAt(now);
                }
                break;
        }

        // Update notes, rating, feedback if provided
        if (dto.getNotes() != null) {
            application.setNotes(dto.getNotes());
        }
        if (dto.getRating() != null) {
            if (dto.getRating() < 1 || dto.getRating() > 5) {
                throw new RuntimeException("Rating must be between 1 and 5");
            }
            application.setRating(dto.getRating());
        }
        if (dto.getFeedback() != null) {
            application.setFeedback(dto.getFeedback());
        }

        CastingCallApplication updated = applicationRepository.save(application);
        log.info("Updated application ID: {} status to {}", applicationId, dto.getStatus());

        // TODO: Send notification to artist

        return convertToResponseDto(updated);
    }

    // 11. Bulk update application statuses
    public BulkUpdateResultDto bulkUpdateApplications(
            User recruiter,
            Long castingCallId,
            BulkUpdateApplicationDto dto) {

        validateRecruiterRole(recruiter);

        CastingCall castingCall = castingCallRepository.findById(castingCallId)
            .orElseThrow(() -> new RuntimeException("Casting call not found"));

        validateOwnership(castingCall, recruiter);

        log.info("Bulk updating {} applications for casting call ID: {} by recruiter: {}",
                 dto.getApplicationIds().size(), castingCallId, recruiter.getEmail());

        List<CastingCallApplication> applications = applicationRepository.findByIdIn(dto.getApplicationIds());

        int successful = 0;
        int failed = 0;
        List<String> errorMessages = new ArrayList<>();
        List<Long> successfulIds = new ArrayList<>();
        List<Long> failedIds = new ArrayList<>();

        for (CastingCallApplication application : applications) {
            try {
                // Verify application belongs to this casting call
                if (!application.getCastingCall().getId().equals(castingCallId)) {
                    throw new RuntimeException("Application does not belong to this casting call");
                }

                // Validate status transition
                validateApplicationStatusTransition(application.getStatus(), dto.getStatus());

                // Update status
                CastingCallApplication.ApplicationStatus oldStatus = application.getStatus();
                application.setStatus(dto.getStatus());

                // Set timestamps
                LocalDateTime now = LocalDateTime.now();
                if (dto.getStatus() == CastingCallApplication.ApplicationStatus.REJECTED) {
                    if (application.getRejectedAt() == null) {
                        application.setRejectedAt(now);
                    }
                    if (dto.getRejectionReason() != null) {
                        application.setRejectionReason(dto.getRejectionReason());
                    }
                } else if (dto.getStatus() == CastingCallApplication.ApplicationStatus.SHORTLISTED) {
                    application.setIsShortlisted(true);
                    if (application.getShortlistedAt() == null) {
                        application.setShortlistedAt(now);
                    }
                }

                if (dto.getNotes() != null) {
                    application.setNotes(dto.getNotes());
                }

                applicationRepository.save(application);
                successful++;
                successfulIds.add(application.getId());

                log.info("Bulk updated application ID: {} from {} to {}",
                         application.getId(), oldStatus, dto.getStatus());

            } catch (Exception e) {
                failed++;
                failedIds.add(application.getId());
                errorMessages.add(String.format("Application %d: %s", application.getId(), e.getMessage()));
                log.error("Failed to bulk update application ID: {}: {}", application.getId(), e.getMessage());
            }
        }

        // Update casting call counters if shortlisted
        if (dto.getStatus() == CastingCallApplication.ApplicationStatus.SHORTLISTED) {
            castingCall.setShortlistedCount(castingCall.getShortlistedCount() + successful);
            castingCallRepository.save(castingCall);
        }

        log.info("Bulk update completed: {} successful, {} failed", successful, failed);

        return BulkUpdateResultDto.builder()
            .totalRequested(dto.getApplicationIds().size())
            .successful(successful)
            .failed(failed)
            .errorMessages(errorMessages)
            .successfulIds(successfulIds)
            .failedIds(failedIds)
            .build();
    }

    // 12. Add private notes to application
    public CastingCallApplicationResponseDto addNotes(
            User recruiter,
            Long castingCallId,
            Long applicationId,
            AddNotesDto dto) {

        validateRecruiterRole(recruiter);

        CastingCall castingCall = castingCallRepository.findById(castingCallId)
            .orElseThrow(() -> new RuntimeException("Casting call not found"));

        validateOwnership(castingCall, recruiter);

        CastingCallApplication application = applicationRepository
            .findByCastingCallIdAndId(castingCallId, applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        log.info("Adding notes to application ID: {} by recruiter: {}", applicationId, recruiter.getEmail());

        // Append notes (don't overwrite)
        String existingNotes = application.getNotes() != null ? application.getNotes() : "";
        String newNotes = existingNotes + "\n\n[" + LocalDateTime.now() + "] " + dto.getNotes();
        application.setNotes(newNotes);

        if (dto.getRating() != null) {
            if (dto.getRating() < 1 || dto.getRating() > 5) {
                throw new RuntimeException("Rating must be between 1 and 5");
            }
            application.setRating(dto.getRating());
        }

        CastingCallApplication updated = applicationRepository.save(application);
        log.info("Added notes to application ID: {}", applicationId);

        return convertToResponseDto(updated);
    }

    // Status transition validation
    private void validateApplicationStatusTransition(
            CastingCallApplication.ApplicationStatus from,
            CastingCallApplication.ApplicationStatus to) {

        Map<CastingCallApplication.ApplicationStatus, List<CastingCallApplication.ApplicationStatus>> validTransitions = Map.of(
            CastingCallApplication.ApplicationStatus.APPLIED,
                List.of(CastingCallApplication.ApplicationStatus.UNDER_REVIEW,
                        CastingCallApplication.ApplicationStatus.REJECTED,
                        CastingCallApplication.ApplicationStatus.WITHDRAWN),

            CastingCallApplication.ApplicationStatus.UNDER_REVIEW,
                List.of(CastingCallApplication.ApplicationStatus.SHORTLISTED,
                        CastingCallApplication.ApplicationStatus.REJECTED,
                        CastingCallApplication.ApplicationStatus.WITHDRAWN),

            CastingCallApplication.ApplicationStatus.SHORTLISTED,
                List.of(CastingCallApplication.ApplicationStatus.CALLBACK_SCHEDULED,
                        CastingCallApplication.ApplicationStatus.SELECTED,
                        CastingCallApplication.ApplicationStatus.REJECTED,
                        CastingCallApplication.ApplicationStatus.WITHDRAWN),

            CastingCallApplication.ApplicationStatus.CALLBACK_SCHEDULED,
                List.of(CastingCallApplication.ApplicationStatus.CALLBACK_COMPLETED,
                        CastingCallApplication.ApplicationStatus.WITHDRAWN),

            CastingCallApplication.ApplicationStatus.CALLBACK_COMPLETED,
                List.of(CastingCallApplication.ApplicationStatus.SELECTED,
                        CastingCallApplication.ApplicationStatus.REJECTED),

            CastingCallApplication.ApplicationStatus.SELECTED, List.of(), // Final state
            CastingCallApplication.ApplicationStatus.REJECTED, List.of(), // Final state
            CastingCallApplication.ApplicationStatus.WITHDRAWN, List.of() // Final state
        );

        if (!validTransitions.get(from).contains(to)) {
            throw new RuntimeException(
                String.format("Invalid status transition from %s to %s", from, to)
            );
        }
    }

    // Helpers
    private void incrementCastingCallCounter(CastingCall castingCall, String counterType) {
        switch (counterType) {
            case "shortlisted":
                castingCall.setShortlistedCount(castingCall.getShortlistedCount() + 1);
                break;
            case "selected":
                castingCall.setSelectedCount(castingCall.getSelectedCount() + 1);
                break;
        }
        castingCallRepository.save(castingCall);
    }

    private void validateRecruiterRole(User user) {
        if (!User.UserRole.RECRUITER.equals(user.getRole())) {
            throw new RuntimeException("Access forbidden: Recruiter role required");
        }
    }

    private void validateOwnership(CastingCall castingCall, User recruiter) {
        if (!castingCall.getRecruiter().getId().equals(recruiter.getId())) {
            throw new RuntimeException("You don't have permission to access this casting call");
        }
    }

    private CastingCallApplicationResponseDto convertToResponseDto(CastingCallApplication app) {
        String artistName = app.getArtist().getFirstName() + " " + app.getArtist().getLastName();

        return CastingCallApplicationResponseDto.builder()
            .id(app.getId())
            .castingCallId(app.getCastingCall().getId())
            .castingCallTitle(app.getCastingCall().getTitle())
            .artistId(app.getArtist().getId())
            .artistName(artistName)
            .artistStageName(app.getArtist().getStageName())
            .artistPhotoUrl(app.getArtist().getPhotoUrl())
            .artistLocation(app.getArtist().getLocation())
            .artistExperienceYears(app.getArtist().getExperienceYears())
            .coverLetter(app.getCoverLetter())
            .auditionVideoUrl(app.getAuditionVideoUrl())
            .resumeUrl(app.getResumeUrl())
            .portfolioUrl(app.getPortfolioUrl())
            .demoReelUrl(app.getDemoReelUrl())
            .status(app.getStatus())
            .appliedAt(app.getAppliedAt())
            .reviewedAt(app.getReviewedAt())
            .shortlistedAt(app.getShortlistedAt())
            .callbackScheduledAt(app.getCallbackScheduledAt())
            .callbackCompletedAt(app.getCallbackCompletedAt())
            .selectedAt(app.getSelectedAt())
            .rejectedAt(app.getRejectedAt())
            .isShortlisted(app.getIsShortlisted())
            .isSelected(app.getIsSelected())
            .notes(app.getNotes())
            .rating(app.getRating())
            .feedback(app.getFeedback())
            .rejectionReason(app.getRejectionReason())
            .callbackDate(app.getCallbackDate())
            .callbackLocation(app.getCallbackLocation())
            .callbackNotes(app.getCallbackNotes())
            .build();
    }
}
