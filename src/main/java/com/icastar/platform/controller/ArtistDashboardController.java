package com.icastar.platform.controller;

import com.icastar.platform.entity.User;
import com.icastar.platform.service.ArtistDashboardService;
import com.icastar.platform.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/artist/dashboard")
@RequiredArgsConstructor
@Slf4j
public class ArtistDashboardController {

    private final ArtistDashboardService artistDashboardService;
    private final UserService userService;

    /**
     * ENDPOINT 1: Get dashboard metrics (6 KPIs with trends)
     * GET /api/artist/dashboard/metrics
     */
    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics(Authentication authentication) {
        try {
            // SECURITY: Extract user from JWT
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/dashboard/metrics - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate artist role
            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/dashboard/metrics by non-artist user: {} (role: {})", email, user.getRole());
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching metrics for artist: {}", email);

            // Call service
            Map<String, Object> metrics = artistDashboardService.getDashboardMetrics(user);

            // Success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", metrics);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching metrics: {}", errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error fetching metrics: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * ENDPOINT 2: Get AI-matched job opportunities
     * GET /api/artist/dashboard/job-opportunities?limit=10
     */
    @GetMapping("/job-opportunities")
    public ResponseEntity<Map<String, Object>> getJobOpportunities(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        try {
            // SECURITY: Extract user from JWT
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/dashboard/job-opportunities - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate artist role
            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/dashboard/job-opportunities by non-artist user: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching job opportunities for artist: {} (limit={})", email, limit);

            // Call service
            List<Map<String, Object>> opportunities = artistDashboardService.getJobOpportunities(user, limit);

            // Success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", opportunities);
            response.put("count", opportunities.size());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching job opportunities: {}", errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error fetching job opportunities: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * ENDPOINT 3: Get profile completion status
     * NOTE: This endpoint has been moved to ArtistProfileDashboardController
     * at the correct path: GET /api/artist/profile/completion
     * (not /api/artist/dashboard/profile-completion)
     */

    /**
     * ENDPOINT 4: Get profile views trend
     * GET /api/artist/dashboard/profile-views-trend
     */
    @GetMapping("/profile-views-trend")
    public ResponseEntity<Map<String, Object>> getProfileViewsTrend(Authentication authentication) {
        try {
            // SECURITY: Extract user from JWT
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/dashboard/profile-views-trend - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate artist role
            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/dashboard/profile-views-trend by non-artist user: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching profile views trend for artist: {}", email);

            // Call service
            Map<String, Object> trend = artistDashboardService.getProfileViewsTrend(user);

            // Success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", trend);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching profile views trend: {}", errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error fetching profile views trend: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * ENDPOINT 5: Get application status breakdown
     * GET /api/artist/dashboard/application-status
     */
    @GetMapping("/application-status")
    public ResponseEntity<Map<String, Object>> getApplicationStatus(Authentication authentication) {
        try {
            // SECURITY: Extract user from JWT
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/dashboard/application-status - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate artist role
            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/dashboard/application-status by non-artist user: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching application status for artist: {}", email);

            // Call service
            Map<String, Object> statusBreakdown = artistDashboardService.getApplicationStatusBreakdown(user);

            // Success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statusBreakdown);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching application status: {}", errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error fetching application status: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * ENDPOINT 6: Get portfolio items
     * GET /api/artist/dashboard/portfolio?limit=6
     */
    @GetMapping("/portfolio")
    public ResponseEntity<Map<String, Object>> getPortfolio(
            @RequestParam(defaultValue = "6") int limit,
            Authentication authentication) {
        try {
            // SECURITY: Extract user from JWT
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/dashboard/portfolio - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate artist role
            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/dashboard/portfolio by non-artist user: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching portfolio for artist: {} (limit={})", email, limit);

            // Call service
            List<Map<String, Object>> portfolio = artistDashboardService.getPortfolioItems(user, limit);

            // Success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", portfolio);
            response.put("count", portfolio.size());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching portfolio: {}", errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error fetching portfolio: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * ENDPOINT 7: Get earnings trend
     * GET /api/artist/dashboard/earnings-trend
     */
    @GetMapping("/earnings-trend")
    public ResponseEntity<Map<String, Object>> getEarningsTrend(Authentication authentication) {
        try {
            // SECURITY: Extract user from JWT
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/dashboard/earnings-trend - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate artist role
            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/dashboard/earnings-trend by non-artist user: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching earnings trend for artist: {}", email);

            // Call service
            Map<String, Object> trend = artistDashboardService.getEarningsTrend(user);

            // Success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", trend);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching earnings trend: {}", errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error fetching earnings trend: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * ENDPOINT 8: Get recent activity timeline
     * GET /api/artist/dashboard/recent-activity?limit=10
     */
    @GetMapping("/recent-activity")
    public ResponseEntity<Map<String, Object>> getRecentActivity(
            @RequestParam(defaultValue = "10") int limit,
            Authentication authentication) {
        try {
            // SECURITY: Extract user from JWT
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/dashboard/recent-activity - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate artist role
            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/dashboard/recent-activity by non-artist user: {}", email);
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            log.info("Fetching recent activity for artist: {} (limit={})", email, limit);

            // Call service
            List<Map<String, Object>> activity = artistDashboardService.getRecentActivity(user, limit);

            // Success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", activity);
            response.put("count", activity.size());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            String errorMessage = e.getMessage();
            log.error("Error fetching recent activity: {}", errorMessage, e);

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", errorMessage);

            if (errorMessage != null && errorMessage.contains("not found")) {
                return ResponseEntity.status(404).body(response);
            }
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            log.error("Unexpected error fetching recent activity: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }
}
