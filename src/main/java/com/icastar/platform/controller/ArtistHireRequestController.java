package com.icastar.platform.controller;

import com.icastar.platform.dto.artist.ArtistRespondHireRequestDto;
import com.icastar.platform.dto.recruiter.HireRequestDto;
import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.HireRequest;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.ArtistService;
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
@RequestMapping("/artist/hire-requests")
@RequiredArgsConstructor
@Slf4j
public class ArtistHireRequestController {

    private final HireRequestService hireRequestService;
    private final UserService userService;
    private final ArtistService artistService;

    /**
     * Get all hire requests received by the artist
     * GET /api/artist/hire-requests
     */
    @GetMapping
    public ResponseEntity<?> getHireRequests(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "sentAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) HireRequest.HireRequestStatus status) {

        try {
            ArtistProfile artist = getAuthenticatedArtist();
            log.info("Fetching hire requests for artist: {}", artist.getId());

            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);

            Page<HireRequest> hireRequests = hireRequestService.getHireRequestsForArtist(
                    artist.getId(), status, pageable);

            Page<HireRequestDto> responseDtos = hireRequests.map(HireRequestDto::new);

            return ResponseEntity.ok(responseDtos);
        } catch (Exception e) {
            log.error("Error fetching hire requests for artist", e);
            return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get single hire request by ID
     * GET /api/artist/hire-requests/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getHireRequest(@PathVariable Long id) {
        try {
            ArtistProfile artist = getAuthenticatedArtist();
            log.info("Fetching hire request {} for artist: {}", id, artist.getId());

            HireRequest hireRequest = hireRequestService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Hire request not found with id: " + id));

            // Verify ownership
            if (!hireRequest.getArtist().getId().equals(artist.getId())) {
                return buildErrorResponse("You don't have permission to view this hire request", HttpStatus.FORBIDDEN);
            }

            // Mark as viewed if pending
            if (hireRequest.getStatus() == HireRequest.HireRequestStatus.PENDING) {
                hireRequest = hireRequestService.markAsViewed(id, artist.getId());
            }

            HireRequestDto responseDto = new HireRequestDto(hireRequest);

            return ResponseEntity.ok(responseDto);
        } catch (Exception e) {
            log.error("Error fetching hire request", e);
            return buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Respond to hire request (Accept or Decline)
     * POST /api/artist/hire-requests/{id}/respond
     */
    @PostMapping("/{id}/respond")
    public ResponseEntity<?> respondToHireRequest(
            @PathVariable Long id,
            @Valid @RequestBody ArtistRespondHireRequestDto dto) {

        try {
            ArtistProfile artist = getAuthenticatedArtist();
            log.info("Artist {} responding to hire request {}", artist.getId(), id);

            // Validate status - only ACCEPTED or DECLINED allowed
            if (dto.getStatus() != HireRequest.HireRequestStatus.ACCEPTED &&
                dto.getStatus() != HireRequest.HireRequestStatus.DECLINED) {
                return buildErrorResponse("Status must be ACCEPTED or DECLINED", HttpStatus.BAD_REQUEST);
            }

            HireRequest hireRequest = hireRequestService.artistRespond(
                    id, artist.getId(), dto.getStatus(), dto.getResponse());

            HireRequestDto responseDto = new HireRequestDto(hireRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", dto.getStatus() == HireRequest.HireRequestStatus.ACCEPTED ?
                    "Hire request accepted successfully" : "Hire request declined");
            response.put("data", responseDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error responding to hire request", e);
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Mark hire request as viewed
     * POST /api/artist/hire-requests/{id}/view
     */
    @PostMapping("/{id}/view")
    public ResponseEntity<?> markAsViewed(@PathVariable Long id) {
        try {
            ArtistProfile artist = getAuthenticatedArtist();
            log.info("Marking hire request {} as viewed by artist: {}", id, artist.getId());

            HireRequest hireRequest = hireRequestService.markAsViewed(id, artist.getId());
            HireRequestDto responseDto = new HireRequestDto(hireRequest);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Hire request marked as viewed");
            response.put("data", responseDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error marking hire request as viewed", e);
            return buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get hire request counts/stats for artist
     * GET /api/artist/hire-requests/counts
     */
    @GetMapping("/counts")
    public ResponseEntity<?> getHireRequestCounts() {
        try {
            ArtistProfile artist = getAuthenticatedArtist();
            log.info("Fetching hire request counts for artist: {}", artist.getId());

            // Get counts for different statuses
            Page<HireRequest> pendingRequests = hireRequestService.getHireRequestsForArtist(
                    artist.getId(), HireRequest.HireRequestStatus.PENDING, PageRequest.of(0, 1));
            Page<HireRequest> viewedRequests = hireRequestService.getHireRequestsForArtist(
                    artist.getId(), HireRequest.HireRequestStatus.VIEWED, PageRequest.of(0, 1));
            Page<HireRequest> acceptedRequests = hireRequestService.getHireRequestsForArtist(
                    artist.getId(), HireRequest.HireRequestStatus.ACCEPTED, PageRequest.of(0, 1));
            Page<HireRequest> allRequests = hireRequestService.getHireRequestsForArtist(
                    artist.getId(), null, PageRequest.of(0, 1));

            Map<String, Object> counts = new HashMap<>();
            counts.put("total", allRequests.getTotalElements());
            counts.put("pending", pendingRequests.getTotalElements());
            counts.put("viewed", viewedRequests.getTotalElements());
            counts.put("accepted", acceptedRequests.getTotalElements());
            counts.put("unread", pendingRequests.getTotalElements()); // pending = unread

            return ResponseEntity.ok(counts);
        } catch (Exception e) {
            log.error("Error fetching hire request counts", e);
            return buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Helper method to get authenticated artist
     */
    private ArtistProfile getAuthenticatedArtist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        User user = userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() != User.UserRole.ARTIST) {
            throw new RuntimeException("Only artists can access this resource");
        }

        return artistService.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Artist profile not found"));
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