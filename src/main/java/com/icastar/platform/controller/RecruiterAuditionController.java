package com.icastar.platform.controller;

import com.icastar.platform.entity.User;
import com.icastar.platform.service.RecruiterAuditionService;
import com.icastar.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/recruiter/open-auditions")
@RequiredArgsConstructor
@Slf4j
public class RecruiterAuditionController {

    private final RecruiterAuditionService recruiterAuditionService;
    private final UserService userService;

    /**
     * Create an open audition with role (artistTypeId)
     * POST /api/recruiter/open-auditions
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAudition(
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to create audition - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!User.UserRole.RECRUITER.equals(user.getRole())) {
                log.warn("Forbidden access attempt to create audition by non-recruiter user: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Recruiter role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Creating audition for recruiter: {}", email);

            Map<String, Object> data = recruiterAuditionService.createAudition(user, body);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Audition created successfully");
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error creating audition: {}", errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error creating audition: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get all open auditions created by recruiter
     * GET /api/recruiter/open-auditions
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyAuditions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean openOnly,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!User.UserRole.RECRUITER.equals(user.getRole())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Recruiter role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching auditions for recruiter: {} (page={}, size={}, openOnly={})", email, page, size, openOnly);

            Map<String, Object> data = recruiterAuditionService.getMyAuditions(user, page, size, openOnly);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error fetching recruiter auditions: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get a specific open audition by ID
     * GET /api/recruiter/open-auditions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAuditionById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!User.UserRole.RECRUITER.equals(user.getRole())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Recruiter role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching audition {} for recruiter: {}", id, email);

            Map<String, Object> data = recruiterAuditionService.getAuditionById(user, id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching audition {}: {}", id, errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error fetching audition {}: {}", id, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Update an open audition
     * PUT /api/recruiter/open-auditions/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateAudition(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!User.UserRole.RECRUITER.equals(user.getRole())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Recruiter role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Updating audition {} for recruiter: {}", id, email);

            Map<String, Object> data = recruiterAuditionService.updateAudition(user, id, body);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Audition updated successfully");
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error updating audition {}: {}", id, errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error updating audition {}: {}", id, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Cancel an open audition
     * POST /api/recruiter/open-auditions/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelAudition(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!User.UserRole.RECRUITER.equals(user.getRole())) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Recruiter role required");
                return ResponseEntity.status(403).body(response);
            }

            String reason = body != null ? body.get("reason") : null;
            log.info("Cancelling audition {} for recruiter: {} (reason: {})", id, email, reason);

            Map<String, Object> data = recruiterAuditionService.cancelAudition(user, id, reason);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Audition cancelled successfully");
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error cancelling audition {}: {}", id, errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error cancelling audition {}: {}", id, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }
}
