package com.icastar.platform.controller;

import com.icastar.platform.dto.castingcall.*;
import com.icastar.platform.entity.CastingCall;
import com.icastar.platform.entity.CastingCallApplication;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.CastingCallApplicationService;
import com.icastar.platform.service.CastingCallService;
import com.icastar.platform.service.CastingCallStatisticsService;
import com.icastar.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/recruiter/auditions")
@RequiredArgsConstructor
@Slf4j
public class CastingCallController {

    private final CastingCallService castingCallService;
    private final CastingCallApplicationService applicationService;
    private final CastingCallStatisticsService statisticsService;
    private final UserService userService;

    // 1. GET /api/recruiter/auditions - List all casting calls with filtering
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllCastingCalls(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String roleType,
            @RequestParam(required = false) String projectType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Boolean isUrgent,
            @RequestParam(required = false) Boolean isFeatured,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        try {
            // SECURITY: Extract user from JWT
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Fetching casting calls for recruiter: {}", email);

            CastingCall.CastingCallStatus statusEnum = null;
            if (status != null) {
                try {
                    statusEnum = CastingCall.CastingCallStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid status: " + status);
                }
            }

            Page<CastingCallResponseDto> castingCalls = castingCallService.getAllCastingCalls(
                user, searchTerm, statusEnum, roleType, projectType, location,
                isUrgent, isFeatured, pageable
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", castingCalls.getContent());
            response.put("totalElements", castingCalls.getTotalElements());
            response.put("totalPages", castingCalls.getTotalPages());
            response.put("currentPage", castingCalls.getNumber());
            response.put("size", castingCalls.getSize());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching casting calls: {}", e.getMessage(), e);
            return buildErrorResponse("Failed to fetch casting calls: " + e.getMessage());
        }
    }

    // 2. GET /api/recruiter/auditions/:id - Get single casting call
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getCastingCallById(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Fetching casting call ID: {} for recruiter: {}", id, email);

            CastingCallResponseDto castingCall = castingCallService.getCastingCallById(user, id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", castingCall);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching casting call: {}", e.getMessage(), e);
            return buildErrorResponse("Failed to fetch casting call: " + e.getMessage());
        }
    }

    // 3. POST /api/recruiter/auditions - Create new casting call
    @PostMapping
    public ResponseEntity<Map<String, Object>> createCastingCall(
            @Valid @RequestBody CreateCastingCallDto createDto,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Creating casting call '{}' for recruiter: {}", createDto.getTitle(), email);

            CastingCallResponseDto castingCall = castingCallService.createCastingCall(user, createDto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Casting call created successfully");
            response.put("data", castingCall);

            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            log.error("Error creating casting call: {}", e.getMessage(), e);
            return buildErrorResponse("Failed to create casting call: " + e.getMessage());
        }
    }

    // 4. PUT /api/recruiter/auditions/:id - Update casting call
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateCastingCall(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCastingCallDto updateDto,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Updating casting call ID: {} for recruiter: {}", id, email);

            CastingCallResponseDto castingCall = castingCallService.updateCastingCall(user, id, updateDto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Casting call updated successfully");
            response.put("data", castingCall);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error updating casting call: {}", e.getMessage(), e);
            return buildErrorResponse("Failed to update casting call: " + e.getMessage());
        }
    }

    // 5. DELETE /api/recruiter/auditions/:id - Delete draft casting call
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteCastingCall(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Deleting casting call ID: {} for recruiter: {}", id, email);

            Map<String, Object> result = castingCallService.deleteCastingCall(user, id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Casting call deleted successfully");
            response.put("data", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error deleting casting call: {}", e.getMessage(), e);
            return buildErrorResponse("Failed to delete casting call: " + e.getMessage());
        }
    }

    // 6. POST /api/recruiter/auditions/:id/publish - Publish casting call
    @PostMapping("/{id}/publish")
    public ResponseEntity<Map<String, Object>> publishCastingCall(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Publishing casting call ID: {} for recruiter: {}", id, email);

            CastingCallResponseDto castingCall = castingCallService.publishCastingCall(user, id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Casting call published successfully");
            response.put("data", castingCall);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error publishing casting call: {}", e.getMessage(), e);
            return buildErrorResponse("Failed to publish casting call: " + e.getMessage());
        }
    }

    // 7. POST /api/recruiter/auditions/:id/close - Close casting call
    @PostMapping("/{id}/close")
    public ResponseEntity<Map<String, Object>> closeCastingCall(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Closing casting call ID: {} for recruiter: {}", id, email);

            CastingCallResponseDto castingCall = castingCallService.closeCastingCall(user, id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Casting call closed successfully");
            response.put("data", castingCall);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error closing casting call: {}", e.getMessage(), e);
            return buildErrorResponse("Failed to close casting call: " + e.getMessage());
        }
    }

    // 8. GET /api/recruiter/auditions/:id/applications - List applications
    @GetMapping("/{id}/applications")
    public ResponseEntity<Map<String, Object>> getApplications(
            @PathVariable Long id,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isShortlisted,
            @RequestParam(required = false) Integer minRating,
            @PageableDefault(size = 20, sort = "appliedAt", direction = Sort.Direction.DESC) Pageable pageable,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Fetching applications for casting call ID: {}", id);

            CastingCallApplication.ApplicationStatus statusEnum = null;
            if (status != null) {
                try {
                    statusEnum = CastingCallApplication.ApplicationStatus.valueOf(status.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new RuntimeException("Invalid status: " + status);
                }
            }

            Page<CastingCallApplicationResponseDto> applications = applicationService.getApplications(
                user, id, statusEnum, isShortlisted, minRating, pageable
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", applications.getContent());
            response.put("totalElements", applications.getTotalElements());
            response.put("totalPages", applications.getTotalPages());
            response.put("currentPage", applications.getNumber());
            response.put("size", applications.getSize());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching applications: {}", e.getMessage(), e);
            return buildErrorResponse("Failed to fetch applications: " + e.getMessage());
        }
    }

    // 9. GET /api/recruiter/auditions/:id/applications/:appId - Get single application
    @GetMapping("/{id}/applications/{appId}")
    public ResponseEntity<Map<String, Object>> getApplicationById(
            @PathVariable Long id,
            @PathVariable Long appId,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Fetching application ID: {} for casting call ID: {}", appId, id);

            CastingCallApplicationResponseDto application =
                applicationService.getApplicationById(user, id, appId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", application);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching application: {}", e.getMessage(), e);
            return buildErrorResponse("Failed to fetch application: " + e.getMessage());
        }
    }

    // 10. PUT /api/recruiter/auditions/:id/applications/:appId/status - Update status
    @PutMapping("/{id}/applications/{appId}/status")
    public ResponseEntity<Map<String, Object>> updateApplicationStatus(
            @PathVariable Long id,
            @PathVariable Long appId,
            @Valid @RequestBody UpdateApplicationStatusDto updateDto,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Updating status for application ID: {} in casting call ID: {}", appId, id);

            CastingCallApplicationResponseDto application =
                applicationService.updateApplicationStatus(user, id, appId, updateDto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Application status updated successfully");
            response.put("data", application);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error updating application status: {}", e.getMessage(), e);
            return buildErrorResponse("Failed to update application status: " + e.getMessage());
        }
    }

    // 11. POST /api/recruiter/auditions/:id/applications/bulk-update - Bulk status update
    @PostMapping("/{id}/applications/bulk-update")
    public ResponseEntity<Map<String, Object>> bulkUpdateApplications(
            @PathVariable Long id,
            @Valid @RequestBody BulkUpdateApplicationDto bulkDto,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Bulk updating {} applications for casting call ID: {}",
                     bulkDto.getApplicationIds().size(), id);

            BulkUpdateResultDto result = applicationService.bulkUpdateApplications(user, id, bulkDto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", String.format(
                "Bulk update completed: %d successful, %d failed",
                result.getSuccessful(), result.getFailed()
            ));
            response.put("data", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error bulk updating applications: {}", e.getMessage(), e);
            return buildErrorResponse("Failed to bulk update applications: " + e.getMessage());
        }
    }

    // 12. PUT /api/recruiter/auditions/:id/applications/:appId/notes - Add notes
    @PutMapping("/{id}/applications/{appId}/notes")
    public ResponseEntity<Map<String, Object>> addNotes(
            @PathVariable Long id,
            @PathVariable Long appId,
            @Valid @RequestBody AddNotesDto notesDto,
            Authentication authentication) {

        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Adding notes to application ID: {} in casting call ID: {}", appId, id);

            CastingCallApplicationResponseDto application =
                applicationService.addNotes(user, id, appId, notesDto);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Notes added successfully");
            response.put("data", application);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error adding notes: {}", e.getMessage(), e);
            return buildErrorResponse("Failed to add notes: " + e.getMessage());
        }
    }

    // 13. GET /api/recruiter/auditions/stats - Get statistics
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics(Authentication authentication) {

        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

            log.info("Fetching statistics for recruiter: {}", email);

            Map<String, Object> statistics = statisticsService.getStatistics(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching statistics: {}", e.getMessage(), e);
            return buildErrorResponse("Failed to fetch statistics: " + e.getMessage());
        }
    }

    // Helper method for error responses
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }
}
