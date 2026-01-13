package com.icastar.platform.service;

import com.icastar.platform.entity.*;
import com.icastar.platform.repository.ArtistTypeRepository;
import com.icastar.platform.repository.AuditionRepository;
import com.icastar.platform.repository.RecruiterProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterAuditionService {

    private final AuditionRepository auditionRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final ArtistTypeRepository artistTypeRepository;

    /**
     * Create an open audition with role (artistTypeId)
     * POST /api/recruiter/auditions
     */
    @Transactional
    public Map<String, Object> createAudition(User recruiter, Map<String, Object> body) {
        try {
            log.info("Creating audition for recruiter: {}", recruiter.getEmail());

            RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                    .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

            // Validate required fields
            if (body.get("artistTypeId") == null) {
                throw new RuntimeException("artistTypeId is required");
            }
            if (body.get("scheduledAt") == null) {
                throw new RuntimeException("scheduledAt is required");
            }
            if (body.get("auditionType") == null) {
                throw new RuntimeException("auditionType is required");
            }

            // Get target artist type
            Long artistTypeId = Long.valueOf(body.get("artistTypeId").toString());
            ArtistType targetArtistType = artistTypeRepository.findById(artistTypeId)
                    .orElseThrow(() -> new RuntimeException("Artist type not found with id: " + artistTypeId));

            // Create audition
            Audition audition = new Audition();
            audition.setRecruiter(recruiterProfile);
            audition.setTargetArtistType(targetArtistType);
            audition.setIsOpenAudition(true);

            // Set title
            if (body.get("title") != null) {
                audition.setTitle(body.get("title").toString());
            } else {
                audition.setTitle("Audition for " + targetArtistType.getName());
            }

            // Set description
            if (body.get("description") != null) {
                audition.setDescription(body.get("description").toString());
            }

            // Set audition type
            String auditionTypeStr = body.get("auditionType").toString().toUpperCase();
            audition.setAuditionType(Audition.AuditionType.valueOf(auditionTypeStr));

            // Set scheduled time
            String scheduledAtStr = body.get("scheduledAt").toString();
            LocalDateTime scheduledAt = LocalDateTime.parse(scheduledAtStr, DateTimeFormatter.ISO_DATE_TIME);
            audition.setScheduledAt(scheduledAt);

            // Set optional fields
            if (body.get("durationMinutes") != null) {
                audition.setDurationMinutes(Integer.valueOf(body.get("durationMinutes").toString()));
            }
            if (body.get("meetingLink") != null) {
                audition.setMeetingLink(body.get("meetingLink").toString());
            }
            if (body.get("instructions") != null) {
                audition.setInstructions(body.get("instructions").toString());
            }

            audition.setStatus(Audition.AuditionStatus.SCHEDULED);
            auditionRepository.save(audition);

            // Build response
            Map<String, Object> auditionData = buildAuditionResponse(audition);

            log.info("Audition {} created successfully for recruiter: {}", audition.getId(), recruiter.getEmail());
            return auditionData;

        } catch (Exception e) {
            log.error("Error creating audition for recruiter {}: {}", recruiter.getEmail(), e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Get all auditions created by recruiter
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getMyAuditions(User recruiter, int page, int size, Boolean openOnly) {
        try {
            log.info("Fetching auditions for recruiter: {}", recruiter.getEmail());

            RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                    .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

            Pageable pageable = PageRequest.of(page, size);
            Page<Audition> auditionsPage;

            if (Boolean.TRUE.equals(openOnly)) {
                auditionsPage = auditionRepository.findOpenAuditionsByRecruiterId(recruiterProfile.getId(), pageable);
            } else {
                auditionsPage = auditionRepository.findByRecruiterId(recruiterProfile.getId(), pageable);
            }

            List<Map<String, Object>> auditionsList = new ArrayList<>();
            for (Audition audition : auditionsPage.getContent()) {
                auditionsList.add(buildAuditionResponse(audition));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("auditions", auditionsList);
            response.put("currentPage", auditionsPage.getNumber());
            response.put("totalPages", auditionsPage.getTotalPages());
            response.put("totalElements", auditionsPage.getTotalElements());
            response.put("hasNext", auditionsPage.hasNext());
            response.put("hasPrevious", auditionsPage.hasPrevious());

            log.info("Found {} auditions for recruiter: {}", auditionsList.size(), recruiter.getEmail());
            return response;

        } catch (Exception e) {
            log.error("Error fetching auditions for recruiter {}: {}", recruiter.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to fetch auditions: " + e.getMessage());
        }
    }

    /**
     * Get audition by ID
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAuditionById(User recruiter, Long auditionId) {
        try {
            log.info("Fetching audition {} for recruiter: {}", auditionId, recruiter.getEmail());

            RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                    .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

            Audition audition = auditionRepository.findById(auditionId)
                    .orElseThrow(() -> new RuntimeException("Audition not found"));

            // Verify ownership
            if (!audition.getRecruiter().getId().equals(recruiterProfile.getId())) {
                throw new RuntimeException("You don't have access to this audition");
            }

            return buildAuditionResponse(audition);

        } catch (Exception e) {
            log.error("Error fetching audition {} for recruiter {}: {}", auditionId, recruiter.getEmail(), e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Update an audition
     */
    @Transactional
    public Map<String, Object> updateAudition(User recruiter, Long auditionId, Map<String, Object> body) {
        try {
            log.info("Updating audition {} for recruiter: {}", auditionId, recruiter.getEmail());

            RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                    .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

            Audition audition = auditionRepository.findById(auditionId)
                    .orElseThrow(() -> new RuntimeException("Audition not found"));

            // Verify ownership
            if (!audition.getRecruiter().getId().equals(recruiterProfile.getId())) {
                throw new RuntimeException("You don't have access to this audition");
            }

            // Update fields
            if (body.get("title") != null) {
                audition.setTitle(body.get("title").toString());
            }
            if (body.get("description") != null) {
                audition.setDescription(body.get("description").toString());
            }
            if (body.get("scheduledAt") != null) {
                LocalDateTime scheduledAt = LocalDateTime.parse(body.get("scheduledAt").toString(), DateTimeFormatter.ISO_DATE_TIME);
                audition.setScheduledAt(scheduledAt);
            }
            if (body.get("durationMinutes") != null) {
                audition.setDurationMinutes(Integer.valueOf(body.get("durationMinutes").toString()));
            }
            if (body.get("meetingLink") != null) {
                audition.setMeetingLink(body.get("meetingLink").toString());
            }
            if (body.get("instructions") != null) {
                audition.setInstructions(body.get("instructions").toString());
            }
            if (body.get("artistTypeId") != null) {
                Long artistTypeId = Long.valueOf(body.get("artistTypeId").toString());
                ArtistType targetArtistType = artistTypeRepository.findById(artistTypeId)
                        .orElseThrow(() -> new RuntimeException("Artist type not found with id: " + artistTypeId));
                audition.setTargetArtistType(targetArtistType);
            }

            auditionRepository.save(audition);

            log.info("Audition {} updated successfully for recruiter: {}", auditionId, recruiter.getEmail());
            return buildAuditionResponse(audition);

        } catch (Exception e) {
            log.error("Error updating audition {} for recruiter {}: {}", auditionId, recruiter.getEmail(), e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Cancel an audition
     */
    @Transactional
    public Map<String, Object> cancelAudition(User recruiter, Long auditionId, String reason) {
        try {
            log.info("Cancelling audition {} for recruiter: {} (reason: {})", auditionId, recruiter.getEmail(), reason);

            RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                    .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

            Audition audition = auditionRepository.findById(auditionId)
                    .orElseThrow(() -> new RuntimeException("Audition not found"));

            // Verify ownership
            if (!audition.getRecruiter().getId().equals(recruiterProfile.getId())) {
                throw new RuntimeException("You don't have access to this audition");
            }

            // Check if can be cancelled
            if (audition.getStatus() != Audition.AuditionStatus.SCHEDULED) {
                throw new RuntimeException("Only scheduled auditions can be cancelled. Current status: " + audition.getStatus().name());
            }

            audition.setStatus(Audition.AuditionStatus.CANCELLED);
            if (reason != null && !reason.isEmpty()) {
                audition.setFeedback("Cancelled by recruiter: " + reason);
            } else {
                audition.setFeedback("Cancelled by recruiter");
            }
            auditionRepository.save(audition);

            Map<String, Object> result = new HashMap<>();
            result.put("id", audition.getId());
            result.put("status", audition.getStatus().name());
            result.put("cancelledAt", LocalDateTime.now());

            log.info("Audition {} cancelled successfully for recruiter: {}", auditionId, recruiter.getEmail());
            return result;

        } catch (Exception e) {
            log.error("Error cancelling audition {} for recruiter {}: {}", auditionId, recruiter.getEmail(), e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Build audition response map
     */
    private Map<String, Object> buildAuditionResponse(Audition audition) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", audition.getId());
        data.put("title", audition.getTitle());
        data.put("description", audition.getDescription());
        data.put("isOpenAudition", audition.getIsOpenAudition());
        data.put("auditionType", audition.getAuditionType() != null ? audition.getAuditionType().name() : null);
        data.put("scheduledAt", audition.getScheduledAt());
        data.put("durationMinutes", audition.getDurationMinutes());
        data.put("meetingLink", audition.getMeetingLink());
        data.put("instructions", audition.getInstructions());
        data.put("status", audition.getStatus() != null ? audition.getStatus().name() : null);
        data.put("createdAt", audition.getCreatedAt());

        // Target artist type (role)
        if (audition.getTargetArtistType() != null) {
            Map<String, Object> artistTypeData = new HashMap<>();
            artistTypeData.put("id", audition.getTargetArtistType().getId());
            artistTypeData.put("name", audition.getTargetArtistType().getName());
            artistTypeData.put("displayName", audition.getTargetArtistType().getDisplayName());
            data.put("targetArtistType", artistTypeData);
        }

        // Artist info (if assigned)
        if (audition.getArtist() != null) {
            Map<String, Object> artistData = new HashMap<>();
            artistData.put("artistId", audition.getArtist().getId());
            artistData.put("firstName", audition.getArtist().getFirstName());
            artistData.put("lastName", audition.getArtist().getLastName());
            data.put("artist", artistData);
        }

        return data;
    }
}
