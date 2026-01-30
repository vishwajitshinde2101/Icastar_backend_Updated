package com.icastar.platform.controller;

import com.icastar.platform.dto.recruiter.CreateHireRequestDto;
import com.icastar.platform.dto.recruiter.HireRequestDto;
import com.icastar.platform.dto.recruiter.HireRequestStatsDto;
import com.icastar.platform.dto.recruiter.UpdateHireRequestStatusDto;
import com.icastar.platform.entity.HireRequest;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.HireRequestService;
import com.icastar.platform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/recruiter/hire-requests")
@RequiredArgsConstructor
@Slf4j
public class RecruiterHireRequestController {

    private final HireRequestService hireRequestService;
    private final UserService userService;

    /**
     * Create a new hire request (send to artist)
     * POST /api/recruiter/hire-requests
     */
    @PostMapping
    public ResponseEntity<?> createHireRequest(@Valid @RequestBody CreateHireRequestDto dto) {
        try {
            User recruiter = getAuthenticatedRecruiter();
            log.info("Creating hire request by recruiter: {} to artist: {}", recruiter.getId(), dto.getArtistId());

            HireRequest hireRequest = hireRequestService.createHireRequest(recruiter, dto);
            HireRequestDto responseDto = new HireRequestDto(hireRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hire request sent successfully");
            response.put("data", responseDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating hire request", e);
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all hire requests with filters
     * GET /api/recruiter/hire-requests
     */
    @GetMapping
    public ResponseEntity<?> getHireRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "sentAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) HireRequest.HireRequestStatus status,
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false) Long artistId,
            @RequestParam(required = false) String artistCategory,
            @RequestParam(required = false) String searchTerm) {

        try {
            User recruiter = getAuthenticatedRecruiter();
            log.info("Fetching hire requests for recruiter: {}", recruiter.getId());

            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<HireRequest> hireRequests = hireRequestService.getHireRequestsByRecruiter(
                    recruiter.getId(), status, jobId, artistId, artistCategory, searchTerm, pageable);

            Page<HireRequestDto> responseDtos = hireRequests.map(HireRequestDto::new);

            return ResponseEntity.ok(responseDtos);
        } catch (Exception e) {
            log.error("Error fetching hire requests", e);
            return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get single hire request by ID
     * GET /api/recruiter/hire-requests/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getHireRequest(@PathVariable Long id) {
        try {
            User recruiter = getAuthenticatedRecruiter();
            log.info("Fetching hire request {} for recruiter: {}", id, recruiter.getId());

            HireRequest hireRequest = hireRequestService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hire request not found with id: " + id));

            // Verify ownership
            if (!hireRequest.getRecruiter().getId().equals(recruiter.getId())) {
                return buildErrorResponse("You don't have permission to view this hire request", HttpStatus.FORBIDDEN);
            }

            HireRequestDto responseDto = new HireRequestDto(hireRequest);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Error fetching hire request", e);
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Update hire request status with notes
     * PATCH /api/recruiter/hire-requests/{id}/status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateHireRequestStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateHireRequestStatusDto dto) {

        try {
            User recruiter = getAuthenticatedRecruiter();
            log.info("Updating status for hire request {} by recruiter: {}", id, recruiter.getId());

            HireRequest hireRequest = hireRequestService.updateStatus(id, recruiter.getId(), dto);
            HireRequestDto responseDto = new HireRequestDto(hireRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hire request status updated successfully");
            response.put("data", responseDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating hire request status", e);
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Withdraw/delete hire request
     * DELETE /api/recruiter/hire-requests/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> withdrawHireRequest(@PathVariable Long id) {
        try {
            User recruiter = getAuthenticatedRecruiter();
            log.info("Withdrawing hire request {} by recruiter: {}", id, recruiter.getId());

            hireRequestService.withdrawHireRequest(id, recruiter.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hire request withdrawn successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error withdrawing hire request", e);
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Send reminder email to artist
     * POST /api/recruiter/hire-requests/{id}/remind
     */
    @PostMapping("/{id}/remind")
    public ResponseEntity<?> sendReminder(@PathVariable Long id) {
        try {
            User recruiter = getAuthenticatedRecruiter();
            log.info("Sending reminder for hire request {} by recruiter: {}", id, recruiter.getId());

            HireRequest hireRequest = hireRequestService.sendReminder(id, recruiter.getId());
            HireRequestDto responseDto = new HireRequestDto(hireRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reminder sent successfully");
            response.put("data", responseDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending reminder", e);
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get hire request statistics
     * GET /api/recruiter/hire-requests/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getHireRequestStats() {
        try {
            User recruiter = getAuthenticatedRecruiter();
            log.info("Fetching hire request stats for recruiter: {}", recruiter.getId());

            HireRequestStatsDto stats = hireRequestService.getStats(recruiter.getId());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching hire request stats", e);
            return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Helper method to get authenticated recruiter
     */
    private User getAuthenticatedRecruiter() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != User.UserRole.RECRUITER) {
            throw new RuntimeException("Only recruiters can access this resource");
        }

        return user;
    }

    /**
     * Helper method to build error response
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return ResponseEntity.status(status).body(response);
    }
}