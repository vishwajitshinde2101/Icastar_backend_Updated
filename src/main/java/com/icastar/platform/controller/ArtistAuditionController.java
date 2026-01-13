package com.icastar.platform.controller;

import com.icastar.platform.entity.User;
import com.icastar.platform.service.ArtistAuditionService;
import com.icastar.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/artist/auditions")
@RequiredArgsConstructor
@Slf4j
public class ArtistAuditionController {

    private final ArtistAuditionService artistAuditionService;
    private final UserService userService;

    /**
     * Get all auditions for the logged-in artist (paginated)
     * GET /api/artist/auditions
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllAuditions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/auditions - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/auditions by non-artist user: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching all auditions for artist: {} (page={}, size={}, status={})", email, page, size, status);

            Map<String, Object> data = artistAuditionService.getAllAuditions(user, page, size, status);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching all auditions: {}", errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error fetching all auditions: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get open auditions matching artist's role (artistType)
     * GET /api/artist/auditions/open
     */
    @GetMapping("/open")
    public ResponseEntity<Map<String, Object>> getOpenAuditionsForMyRole(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/auditions/open - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/auditions/open by non-artist user: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching open auditions for artist: {} (page={}, size={})", email, page, size);

            Map<String, Object> data = artistAuditionService.getOpenAuditionsForMyRole(user, page, size);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching open auditions: {}", errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error fetching open auditions: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get upcoming auditions for the logged-in artist
     * GET /api/artist/auditions/upcoming
     */
    @GetMapping("/upcoming")
    public ResponseEntity<Map<String, Object>> getUpcomingAuditions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/auditions/upcoming - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/auditions/upcoming by non-artist user: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching upcoming auditions for artist: {} (page={}, size={})", email, page, size);

            Map<String, Object> data = artistAuditionService.getUpcomingAuditions(user, page, size);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching upcoming auditions: {}", errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error fetching upcoming auditions: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get past auditions for the logged-in artist
     * GET /api/artist/auditions/past
     */
    @GetMapping("/past")
    public ResponseEntity<Map<String, Object>> getPastAuditions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/auditions/past - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/auditions/past by non-artist user: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching past auditions for artist: {} (page={}, size={})", email, page, size);

            Map<String, Object> data = artistAuditionService.getPastAuditions(user, page, size);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching past auditions: {}", errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error fetching past auditions: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get a specific audition by ID
     * GET /api/artist/auditions/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getAuditionById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/auditions/{} - no authentication", id);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/auditions/{} by non-artist user: {}", id, email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching audition {} for artist: {}", id, email);

            Map<String, Object> data = artistAuditionService.getAuditionById(user, id);

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
            if (errorMessage != null && errorMessage.contains("don't have access")) {
                return ResponseEntity.status(403).body(response);
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
     * Cancel an audition
     * POST /api/artist/auditions/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelAudition(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body,
            Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to cancel audition {} - no authentication", id);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to cancel audition {} by non-artist user: {}", id, email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            String reason = body != null ? body.get("reason") : null;
            log.info("Cancelling audition {} for artist: {} (reason: {})", id, email, reason);

            Map<String, Object> data = artistAuditionService.cancelAudition(user, id, reason);

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

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            if (errorMessage != null && errorMessage.contains("don't have access")) {
                return ResponseEntity.status(403).body(response);
            }
            if (errorMessage != null && errorMessage.contains("cannot be cancelled")) {
                return ResponseEntity.badRequest().body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error cancelling audition {}: {}", id, e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Get audition statistics for artist
     * GET /api/artist/auditions/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAuditionStats(Authentication authentication) {
        try {
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/auditions/stats - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/auditions/stats by non-artist user: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching audition stats for artist: {}", email);

            Map<String, Object> data = artistAuditionService.getAuditionStats(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", data);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching audition stats: {}", errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error fetching audition stats: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }
}
