package com.icastar.platform.service;

import com.icastar.platform.entity.Audition;
import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.CastingCall;
import com.icastar.platform.entity.User;
import com.icastar.platform.repository.ArtistProfileRepository;
import com.icastar.platform.repository.AuditionRepository;
import com.icastar.platform.repository.CastingCallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistAuditionService {

    private final AuditionRepository auditionRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final CastingCallRepository castingCallRepository;

    /**
     * Get all open auditions (paginated with optional status filter)
     * GET /api/artist/auditions
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAllAuditions(User artist, int page, int size, String status) {
        try {
            log.info("Fetching all auditions for artist: {} (status={})", artist.getEmail(), status);

            ArtistProfile artistProfile = artistProfileRepository.findByUserId(artist.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            Pageable pageable = PageRequest.of(page, size);
            LocalDateTime now = LocalDateTime.now();

            // Get all open auditions
            Page<Audition> auditionsPage = auditionRepository.findAllOpenAuditions(now, pageable);

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

            log.info("Found {} auditions for artist: {}", auditionsList.size(), artist.getEmail());
            return response;

        } catch (Exception e) {
            log.error("Error fetching all auditions for artist {}: {}", artist.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to fetch auditions: " + e.getMessage());
        }
    }

    /**
     * Get upcoming open auditions for an artist
     * GET /api/artist/auditions/upcoming
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUpcomingAuditions(User artist, int page, int size) {
        try {
            log.info("Fetching upcoming auditions for artist: {}", artist.getEmail());

            ArtistProfile artistProfile = artistProfileRepository.findByUserId(artist.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            LocalDateTime now = LocalDateTime.now();
            Pageable pageable = PageRequest.of(page, size);

            // Get upcoming open auditions (scheduled and in future)
            Page<Audition> auditionsPage = auditionRepository.findAllOpenAuditions(now, pageable);

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

            log.info("Found {} upcoming auditions for artist: {}", auditionsList.size(), artist.getEmail());
            return response;

        } catch (Exception e) {
            log.error("Error fetching upcoming auditions for artist {}: {}", artist.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to fetch upcoming auditions: " + e.getMessage());
        }
    }

    /**
     * Get past auditions for an artist (completed/cancelled)
     * GET /api/artist/auditions/past
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPastAuditions(User artist, int page, int size) {
        try {
            log.info("Fetching past auditions for artist: {}", artist.getEmail());

            ArtistProfile artistProfile = artistProfileRepository.findByUserId(artist.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            LocalDateTime now = LocalDateTime.now();
            Pageable pageable = PageRequest.of(page, size);

            // Get past auditions for this artist
            Page<Audition> auditionsPage = auditionRepository.findPastByArtistId(artistProfile.getId(), now, pageable);

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

            log.info("Found {} past auditions for artist: {}", auditionsList.size(), artist.getEmail());
            return response;

        } catch (Exception e) {
            log.error("Error fetching past auditions for artist {}: {}", artist.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to fetch past auditions: " + e.getMessage());
        }
    }

    /**
     * Get audition by ID for an artist
     * GET /api/artist/auditions/{id}
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAuditionById(User artist, Long auditionId) {
        try {
            log.info("Fetching audition {} for artist: {}", auditionId, artist.getEmail());

            ArtistProfile artistProfile = artistProfileRepository.findByUserId(artist.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            Audition audition = auditionRepository.findById(auditionId)
                    .orElseThrow(() -> new RuntimeException("Audition not found"));

            Map<String, Object> auditionData = buildAuditionResponse(audition);

            log.info("Audition {} fetched for artist: {}", auditionId, artist.getEmail());
            return auditionData;

        } catch (Exception e) {
            log.error("Error fetching audition {} for artist {}: {}", auditionId, artist.getEmail(), e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Cancel an audition for an artist
     * POST /api/artist/auditions/{id}/cancel
     */
    @Transactional
    public Map<String, Object> cancelAudition(User artist, Long auditionId, String reason) {
        try {
            log.info("Cancelling audition {} for artist: {} (reason: {})", auditionId, artist.getEmail(), reason);

            ArtistProfile artistProfile = artistProfileRepository.findByUserId(artist.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            Audition audition = auditionRepository.findById(auditionId)
                    .orElseThrow(() -> new RuntimeException("Audition not found"));

            // Verify the audition belongs to this artist
            if (audition.getArtist() == null || !audition.getArtist().getId().equals(artistProfile.getId())) {
                throw new RuntimeException("You don't have access to this audition");
            }

            // Check if audition can be cancelled (only SCHEDULED auditions can be cancelled)
            if (audition.getStatus() != Audition.AuditionStatus.SCHEDULED) {
                throw new RuntimeException("Only scheduled auditions can be cancelled. Current status: " + audition.getStatus().name());
            }

            // Update the audition status to CANCELLED
            audition.setStatus(Audition.AuditionStatus.CANCELLED);
            if (reason != null && !reason.isEmpty()) {
                audition.setFeedback("Cancelled by artist: " + reason);
            } else {
                audition.setFeedback("Cancelled by artist");
            }
            auditionRepository.save(audition);

            Map<String, Object> auditionData = new HashMap<>();
            auditionData.put("id", audition.getId());
            auditionData.put("status", audition.getStatus().name());
            auditionData.put("cancelledAt", LocalDateTime.now());

            log.info("Audition {} cancelled successfully for artist: {}", auditionId, artist.getEmail());
            return auditionData;

        } catch (Exception e) {
            log.error("Error cancelling audition {} for artist {}: {}", auditionId, artist.getEmail(), e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Get audition statistics for artist dashboard
     * GET /api/artist/auditions/stats
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAuditionStats(User artist) {
        try {
            log.info("Fetching audition stats for artist: {}", artist.getEmail());

            ArtistProfile artistProfile = artistProfileRepository.findByUserId(artist.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            LocalDateTime now = LocalDateTime.now();
            LocalDate today = LocalDate.now();

            // Count upcoming auditions for this artist
            Long upcomingCount = auditionRepository.countUpcomingByArtistId(artistProfile.getId(), now);
            Long completedCount = auditionRepository.countCompletedByArtistId(artistProfile.getId());

            // Count all open casting calls
            Page<CastingCall> openCastingCalls = castingCallRepository.findAllOpenCastingCallsForArtist(
                    today, PageRequest.of(0, 1));
            Long openCastingCallsCount = openCastingCalls.getTotalElements();

            Map<String, Object> stats = new HashMap<>();
            stats.put("upcomingAuditions", upcomingCount);
            stats.put("completedAuditions", completedCount);
            stats.put("totalAuditions", upcomingCount + completedCount);
            stats.put("openAuditionsForMyRole", openCastingCallsCount);
            stats.put("openCastingCalls", openCastingCallsCount);
            stats.put("artistType", artistProfile.getArtistType() != null ? artistProfile.getArtistType().getDisplayName() : null);

            log.info("Audition stats fetched for artist: {}", artist.getEmail());
            return stats;

        } catch (Exception e) {
            log.error("Error fetching audition stats for artist {}: {}", artist.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to fetch audition stats: " + e.getMessage());
        }
    }

    /**
     * Get open auditions/casting calls for artists
     * GET /api/artist/auditions/open
     *
     * This returns ALL open casting calls where:
     * - status = OPEN
     * - auditionDeadline >= today (or null)
     * - deletedAt IS NULL
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getOpenAuditionsForMyRole(User artist, int page, int size) {
        try {
            log.info("Fetching open casting calls for artist: {}", artist.getEmail());

            ArtistProfile artistProfile = artistProfileRepository.findByUserId(artist.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            LocalDate today = LocalDate.now();
            Pageable pageable = PageRequest.of(page, size);

            // Get ALL open casting calls (not filtered by artist type)
            Page<CastingCall> castingCallsPage = castingCallRepository.findAllOpenCastingCallsForArtist(today, pageable);

            String artistRole = artistProfile.getArtistType() != null ?
                    artistProfile.getArtistType().getDisplayName() : null;

            List<Map<String, Object>> auditionsList = new ArrayList<>();
            for (CastingCall castingCall : castingCallsPage.getContent()) {
                auditionsList.add(buildCastingCallResponse(castingCall));
            }

            Map<String, Object> response = new HashMap<>();
            response.put("auditions", auditionsList);
            response.put("currentPage", castingCallsPage.getNumber());
            response.put("totalPages", castingCallsPage.getTotalPages());
            response.put("totalElements", castingCallsPage.getTotalElements());
            response.put("hasNext", castingCallsPage.hasNext());
            response.put("hasPrevious", castingCallsPage.hasPrevious());
            response.put("artistRole", artistRole);

            log.info("Found {} open casting calls for artist: {} (role: {})",
                    auditionsList.size(), artist.getEmail(), artistRole);
            return response;

        } catch (Exception e) {
            log.error("Error fetching open casting calls for artist {}: {}", artist.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to fetch open auditions: " + e.getMessage());
        }
    }

    /**
     * Build response map from CastingCall entity
     */
    private Map<String, Object> buildCastingCallResponse(CastingCall castingCall) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", castingCall.getId());
        data.put("title", castingCall.getTitle());
        data.put("description", castingCall.getDescription());
        data.put("roleType", castingCall.getRoleType());
        data.put("projectType", castingCall.getProjectType());
        data.put("projectName", castingCall.getProjectName());
        data.put("characterName", castingCall.getCharacterName());
        data.put("location", castingCall.getLocation());
        data.put("auditionDate", castingCall.getAuditionDate());
        data.put("auditionDeadline", castingCall.getAuditionDeadline());
        data.put("auditionFormat", castingCall.getAuditionFormat() != null ? castingCall.getAuditionFormat().name() : null);
        data.put("auditionLocation", castingCall.getAuditionLocation());
        data.put("status", castingCall.getStatus() != null ? castingCall.getStatus().name() : null);
        data.put("isOpenAudition", true);
        data.put("isUrgent", castingCall.getIsUrgent());
        data.put("isFeatured", castingCall.getIsFeatured());
        data.put("acceptsRemoteAuditions", castingCall.getAcceptsRemoteAuditions());
        data.put("requiresVideoAudition", castingCall.getRequiresVideoAudition());

        // Age requirements
        data.put("ageRangeMin", castingCall.getAgeRangeMin());
        data.put("ageRangeMax", castingCall.getAgeRangeMax());
        data.put("genderPreference", castingCall.getGenderPreference() != null ? castingCall.getGenderPreference().name() : null);

        // Compensation
        data.put("compensationMin", castingCall.getCompensationMin());
        data.put("compensationMax", castingCall.getCompensationMax());
        data.put("currency", castingCall.getCurrency());
        data.put("isPaid", castingCall.getIsPaid());

        // Stats
        data.put("applicationsCount", castingCall.getApplicationsCount());
        data.put("viewsCount", castingCall.getViewsCount());

        data.put("createdAt", castingCall.getCreatedAt());
        data.put("publishedAt", castingCall.getPublishedAt());

        // Recruiter details
        if (castingCall.getRecruiter() != null) {
            Map<String, Object> recruiterDetails = new HashMap<>();
            recruiterDetails.put("recruiterId", castingCall.getRecruiter().getId());
            recruiterDetails.put("recruiterName", castingCall.getRecruiter().getFirstName() + " " +
                    (castingCall.getRecruiter().getLastName() != null ? castingCall.getRecruiter().getLastName() : ""));
            recruiterDetails.put("email", castingCall.getRecruiter().getEmail());
            data.put("recruiter", recruiterDetails);
        }

        return data;
    }

    /**
     * Build response map from Audition entity
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
        data.put("completedAt", audition.getCompletedAt());
        data.put("feedback", audition.getFeedback());
        data.put("rating", audition.getRating());
        data.put("recordingUrl", audition.getRecordingUrl());
        data.put("createdAt", audition.getCreatedAt());

        // Target artist type (role for open auditions)
        if (audition.getTargetArtistType() != null) {
            Map<String, Object> artistTypeData = new HashMap<>();
            artistTypeData.put("id", audition.getTargetArtistType().getId());
            artistTypeData.put("name", audition.getTargetArtistType().getName());
            artistTypeData.put("displayName", audition.getTargetArtistType().getDisplayName());
            data.put("targetArtistType", artistTypeData);
        }

        // Recruiter details
        if (audition.getRecruiter() != null) {
            Map<String, Object> recruiterDetails = new HashMap<>();
            recruiterDetails.put("recruiterId", audition.getRecruiter().getId());
            recruiterDetails.put("companyName", audition.getRecruiter().getCompanyName());
            recruiterDetails.put("companyLogo", audition.getRecruiter().getCompanyLogoUrl());
            data.put("recruiter", recruiterDetails);
        }

        // Job details (if linked to a job application)
        if (audition.getJobApplication() != null && audition.getJobApplication().getJob() != null) {
            Map<String, Object> jobDetails = new HashMap<>();
            jobDetails.put("jobId", audition.getJobApplication().getJob().getId());
            jobDetails.put("title", audition.getJobApplication().getJob().getTitle());
            jobDetails.put("location", audition.getJobApplication().getJob().getLocation());
            data.put("job", jobDetails);
        }

        return data;
    }
}
