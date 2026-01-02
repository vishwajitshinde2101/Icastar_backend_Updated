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
import java.util.Map;

/**
 * REST Controller for Artist Profile Dashboard endpoints
 * Base path: /api/artist/profile
 *
 * This controller handles profile-specific dashboard endpoints that don't fit
 * under the main /artist/dashboard base path.
 */
@RestController
@RequestMapping("/artist/profile")
@RequiredArgsConstructor
@Slf4j
public class ArtistProfileDashboardController {

    private final ArtistDashboardService artistDashboardService;
    private final UserService userService;

    /**
     * Get profile completion percentage and recommendations
     * Endpoint: GET /api/artist/profile/completion
     *
     * Returns:
     * - completionPercentage: int (0-100)
     * - isProfileComplete: boolean
     * - missingFields: String[] (empty if complete)
     * - recommendations: String[] (suggestions to improve profile)
     *
     * @param authentication Spring Security authentication context (from JWT)
     * @return Profile completion data with HTTP 200, or error with appropriate status code
     */
    @GetMapping("/completion")
    public ResponseEntity<Map<String, Object>> getProfileCompletion(Authentication authentication) {
        try {
            // SECURITY: Extract user from JWT
            if (authentication == null || authentication.getName() == null) {
                log.warn("Unauthorized access attempt to /artist/profile/completion - no authentication");
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Authentication required");
                return ResponseEntity.status(401).body(response);
            }

            String email = authentication.getName();
            log.info("Artist {} requested profile completion data", email);

            // Find user by email from JWT
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Validate artist role
            if (!User.UserRole.ARTIST.equals(user.getRole())) {
                log.warn("Forbidden access attempt to /artist/profile/completion by non-artist user: {} (role: {})",
                        email, user.getRole());
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist role required");
                return ResponseEntity.status(403).body(response);
            }

            // Get profile completion data from service
            Map<String, Object> completionData = artistDashboardService.getProfileCompletion(user);

            // Build success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", completionData);

            log.info("Successfully retrieved profile completion for artist: {} (completion: {}%)",
                    email, completionData.get("completionPercentage"));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Runtime error fetching profile completion: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error fetching profile completion: " + e.getMessage());
            return ResponseEntity.status(500).body(response);

        } catch (Exception e) {
            log.error("Unexpected error in getProfileCompletion: {}", e.getMessage(), e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }
}
