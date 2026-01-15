package com.icastar.platform.controller;

import com.icastar.platform.entity.Audition;
import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.ArtistService;
import com.icastar.platform.service.AuditionService;
import com.icastar.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/artist/auditions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Artist Auditions", description = "APIs for artist audition management")
public class ArtistAuditionController {

    private final AuditionService auditionService;
    private final ArtistService artistService;
    private final UserService userService;

    /**
     * Get all auditions for the authenticated artist
     * GET /api/artist/auditions
     */
    @Operation(summary = "Get all auditions", description = "Get all auditions for the authenticated artist with pagination")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAuditions(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Filter by status") @RequestParam(required = false) String status,
            @Parameter(description = "Filter by type") @RequestParam(required = false) String type) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "scheduledAt"));
            Page<Audition> auditionsPage;

            // Filter by status if provided
            if (status != null && !status.trim().isEmpty()) {
                try {
                    Audition.AuditionStatus auditionStatus = Audition.AuditionStatus.valueOf(status.toUpperCase());
                    auditionsPage = auditionService.getAuditionsByStatus(artistProfile, auditionStatus, pageable);
                } catch (IllegalArgumentException e) {
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("success", false);
                    response.put("message", "Invalid status: " + status);
                    return ResponseEntity.badRequest().body(response);
                }
            }
            // Filter by type if provided
            else if (type != null && !type.trim().isEmpty()) {
                try {
                    Audition.AuditionType auditionType = Audition.AuditionType.valueOf(type.toUpperCase());
                    auditionsPage = auditionService.getAuditionsByType(artistProfile, auditionType, pageable);
                } catch (IllegalArgumentException e) {
                    Map<String, Object> response = new LinkedHashMap<>();
                    response.put("success", false);
                    response.put("message", "Invalid type: " + type);
                    return ResponseEntity.badRequest().body(response);
                }
            } else {
                auditionsPage = auditionService.getAuditionsByArtist(artistProfile, pageable);
            }

            // Transform auditions to response format
            List<Map<String, Object>> auditions = auditionsPage.getContent().stream()
                    .map(this::transformAudition)
                    .collect(Collectors.toList());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("data", auditions);
            response.put("totalElements", auditionsPage.getTotalElements());
            response.put("totalPages", auditionsPage.getTotalPages());
            response.put("currentPage", auditionsPage.getNumber());
            response.put("hasMore", auditionsPage.hasNext());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving auditions", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch auditions: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get upcoming auditions for the authenticated artist
     * GET /api/artist/auditions/upcoming
     */
    @Operation(summary = "Get upcoming auditions", description = "Get all upcoming auditions for the authenticated artist")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/upcoming")
    public ResponseEntity<Map<String, Object>> getUpcomingAuditions() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            List<Audition> upcomingAuditions = auditionService.getUpcomingAuditions(artistProfile);

            // Transform auditions to response format
            List<Map<String, Object>> auditions = upcomingAuditions.stream()
                    .map(this::transformAudition)
                    .collect(Collectors.toList());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("data", auditions);
            response.put("count", auditions.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving upcoming auditions", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch upcoming auditions: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get past auditions for the authenticated artist
     * GET /api/artist/auditions/past
     */
    @Operation(summary = "Get past auditions", description = "Get all past auditions for the authenticated artist")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/past")
    public ResponseEntity<Map<String, Object>> getPastAuditions(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            Pageable pageable = PageRequest.of(page, size);
            Page<Audition> auditionsPage = auditionService.getPastAuditions(artistProfile, pageable);

            // Transform auditions to response format
            List<Map<String, Object>> auditions = auditionsPage.getContent().stream()
                    .map(this::transformAudition)
                    .collect(Collectors.toList());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("data", auditions);
            response.put("totalElements", auditionsPage.getTotalElements());
            response.put("totalPages", auditionsPage.getTotalPages());
            response.put("currentPage", auditionsPage.getNumber());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving past auditions", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch past auditions: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get audition by ID
     * GET /api/artist/auditions/{id}
     */
    @Operation(summary = "Get audition details", description = "Get details of a specific audition")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAuditionById(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            Audition audition = auditionService.getAuditionById(id)
                    .orElseThrow(() -> new RuntimeException("Audition not found"));

            // Verify the audition belongs to the artist
            if (!audition.getArtist().getId().equals(artistProfile.getId())) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("success", false);
                response.put("message", "Unauthorized access to audition");
                return ResponseEntity.status(403).body(response);
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("data", transformAudition(audition));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving audition details", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch audition details: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Update audition status (cancel, mark as completed, etc.)
     * PUT /api/artist/auditions/{id}/status
     */
    @Operation(summary = "Update audition status", description = "Update the status of an audition")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateAuditionStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            String statusStr = request.get("status");
            if (statusStr == null || statusStr.trim().isEmpty()) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("success", false);
                response.put("message", "Status is required");
                return ResponseEntity.badRequest().body(response);
            }

            Audition.AuditionStatus status;
            try {
                status = Audition.AuditionStatus.valueOf(statusStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                Map<String, Object> response = new LinkedHashMap<>();
                response.put("success", false);
                response.put("message", "Invalid status: " + statusStr);
                return ResponseEntity.badRequest().body(response);
            }

            Audition audition = auditionService.updateAuditionStatus(id, status, artistProfile);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("message", "Audition status updated successfully");
            response.put("data", transformAudition(audition));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating audition status", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update audition status: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Cancel an audition
     * POST /api/artist/auditions/{id}/cancel
     */
    @Operation(summary = "Cancel audition", description = "Cancel an audition")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelAudition(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            Audition audition = auditionService.cancelAudition(id, artistProfile);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("message", "Audition cancelled successfully");
            response.put("data", transformAudition(audition));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error cancelling audition", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to cancel audition: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get audition statistics
     * GET /api/artist/auditions/stats
     */
    @Operation(summary = "Get audition statistics", description = "Get statistics about artist's auditions")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAuditionStats() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            Long upcomingCount = auditionService.countUpcomingAuditions(artistProfile);

            Map<String, Object> stats = new LinkedHashMap<>();
            stats.put("upcomingAuditions", upcomingCount);
            stats.put("hasUpcomingAuditions", upcomingCount > 0);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("data", stats);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving audition stats", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch audition statistics: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Helper method to transform Audition entity to response format
     */
    private Map<String, Object> transformAudition(Audition audition) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        Map<String, Object> auditionMap = new LinkedHashMap<>();
        auditionMap.put("id", audition.getId());
        auditionMap.put("type", audition.getAuditionType().toString());
        auditionMap.put("status", audition.getStatus().toString());
        auditionMap.put("scheduledAt", audition.getScheduledAt() != null ? audition.getScheduledAt().format(formatter) : null);
        auditionMap.put("durationMinutes", audition.getDurationMinutes());
        auditionMap.put("meetingLink", audition.getMeetingLink());
        auditionMap.put("instructions", audition.getInstructions());
        auditionMap.put("feedback", audition.getFeedback());
        auditionMap.put("rating", audition.getRating());
        auditionMap.put("recordingUrl", audition.getRecordingUrl());
        auditionMap.put("completedAt", audition.getCompletedAt() != null ? audition.getCompletedAt().format(formatter) : null);

        // Add job application details if available
        if (audition.getJobApplication() != null && audition.getJobApplication().getJob() != null) {
            Map<String, Object> jobDetails = new LinkedHashMap<>();
            jobDetails.put("jobId", audition.getJobApplication().getJob().getId());
            jobDetails.put("jobTitle", audition.getJobApplication().getJob().getTitle());
            jobDetails.put("company", audition.getJobApplication().getJob().getRecruiter().getRecruiterProfile() != null
                    ? audition.getJobApplication().getJob().getRecruiter().getRecruiterProfile().getCompanyName()
                    : "Unknown Company");
            jobDetails.put("location", audition.getJobApplication().getJob().getLocation());
            auditionMap.put("job", jobDetails);
        }

        // Add recruiter details if available
        if (audition.getRecruiter() != null) {
            Map<String, Object> recruiterDetails = new LinkedHashMap<>();
            recruiterDetails.put("id", audition.getRecruiter().getId());
            recruiterDetails.put("companyName", audition.getRecruiter().getCompanyName());
            auditionMap.put("recruiter", recruiterDetails);
        }

        return auditionMap;
    }
}
