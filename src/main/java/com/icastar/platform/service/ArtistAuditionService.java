package com.icastar.platform.service;

import com.icastar.platform.entity.Audition;
import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.User;
import com.icastar.platform.repository.ArtistProfileRepository;
import com.icastar.platform.repository.AuditionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArtistAuditionService {

    private final AuditionRepository auditionRepository;
    private final ArtistProfileRepository artistProfileRepository;

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

            // Count upcoming auditions for this artist
            Long upcomingCount = auditionRepository.countUpcomingByArtistId(artistProfile.getId(), now);
            Long completedCount = auditionRepository.countCompletedByArtistId(artistProfile.getId());

            // Count open auditions matching artist's role
            Long openAuditionsForMyRole = 0L;
            if (artistProfile.getArtistType() != null) {
                openAuditionsForMyRole = auditionRepository.countOpenAuditionsByArtistTypeId(
                        artistProfile.getArtistType().getId(), now);
            }

            Map<String, Object> stats = new HashMap<>();
            stats.put("upcomingAuditions", upcomingCount);
            stats.put("completedAuditions", completedCount);
            stats.put("totalAuditions", upcomingCount + completedCount);
            stats.put("openAuditionsForMyRole", openAuditionsForMyRole);
            stats.put("artistType", artistProfile.getArtistType() != null ? artistProfile.getArtistType().getDisplayName() : null);

            log.info("Audition stats fetched for artist: {}", artist.getEmail());
            return stats;

        } catch (Exception e) {
            log.error("Error fetching audition stats for artist {}: {}", artist.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to fetch audition stats: " + e.getMessage());
        }
    }

    /**
     * Get open auditions matching artist's role (artistType)
     * GET /api/artist/auditions/open
     *
     * This returns auditions where:
     * - isOpenAudition = true
     * - targetArtistType.id matches artist's artistType.id
     * - status = SCHEDULED
     * - scheduledAt >= now
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getOpenAuditionsForMyRole(User artist, int page, int size) {
        try {
            log.info("Fetching open auditions for artist: {}", artist.getEmail());

            ArtistProfile artistProfile = artistProfileRepository.findByUserId(artist.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            LocalDateTime now = LocalDateTime.now();
            Pageable pageable = PageRequest.of(page, size);

            Page<Audition> auditionsPage;
            String artistRole = null;

            if (artistProfile.getArtistType() != null) {
                artistRole = artistProfile.getArtistType().getDisplayName();
                Long artistTypeId = artistProfile.getArtistType().getId();
                log.info("Fetching open auditions for artistTypeId: {} ({})", artistTypeId, artistRole);

                // Query open auditions matching artist's role
                auditionsPage = auditionRepository.findOpenAuditionsByArtistTypeId(artistTypeId, now, pageable);
            } else {
                // If no role set, show all open auditions
                log.info("No role set for artist, fetching all open auditions");
                auditionsPage = auditionRepository.findAllOpenAuditions(now, pageable);
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
            response.put("artistRole", artistRole);

            log.info("Found {} open auditions for artist: {} (role: {})",
                    auditionsList.size(), artist.getEmail(), artistRole);
            return response;

        } catch (Exception e) {
            log.error("Error fetching open auditions for artist {}: {}", artist.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Failed to fetch open auditions: " + e.getMessage());
        }
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
