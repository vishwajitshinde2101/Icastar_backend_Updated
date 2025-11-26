package com.icastar.platform.controller;

import com.icastar.platform.constants.ApplicationConstants;
import com.icastar.platform.dto.job.BookmarkedJobDto;
import com.icastar.platform.dto.job.CreateBookmarkDto;
import com.icastar.platform.entity.BookmarkedJob;
import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.BookmarkedJobService;
import com.icastar.platform.service.ArtistService;
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

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookmarks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Bookmarked Jobs Management", description = "APIs for managing bookmarked jobs")
public class BookmarkedJobController {

    private final BookmarkedJobService bookmarkedJobService;
    private final ArtistService artistService;
    private final UserService userService;

    @Operation(summary = "Bookmark a job", description = "Add a job to bookmarks")
    @SecurityRequirement(name = "bearerAuth")
    @PostMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> bookmarkJob(
            @PathVariable Long jobId,
            @Valid @RequestBody(required = false) CreateBookmarkDto createDto) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.ARTIST_PROFILE_NOT_FOUND));

            CreateBookmarkDto bookmarkDto = createDto != null ? createDto : new CreateBookmarkDto();
            BookmarkedJob bookmark = bookmarkedJobService.bookmarkJob(artistProfile.getId(), jobId, bookmarkDto);

            // Convert entity to DTO to avoid Jackson serialization issues
            BookmarkedJobDto bookmarkResponseDto = new BookmarkedJobDto();
            bookmarkResponseDto.setId(bookmark.getId());
            bookmarkResponseDto.setJobId(bookmark.getJob().getId());
            bookmarkResponseDto.setJobTitle(bookmark.getJob().getTitle());
            bookmarkResponseDto.setJobDescription(bookmark.getJob().getDescription());
            bookmarkResponseDto.setJobLocation(bookmark.getJob().getLocation());
            bookmarkResponseDto.setJobType(bookmark.getJob().getJobType().toString());
            bookmarkResponseDto.setExperienceLevel(bookmark.getJob().getExperienceLevel().toString());
            bookmarkResponseDto.setBudgetMin(bookmark.getJob().getBudgetMin());
            bookmarkResponseDto.setBudgetMax(bookmark.getJob().getBudgetMax());
            bookmarkResponseDto.setCurrency(bookmark.getJob().getCurrency());
            bookmarkResponseDto.setIsRemote(bookmark.getJob().getIsRemote());
            bookmarkResponseDto.setIsUrgent(bookmark.getJob().getIsUrgent());
            bookmarkResponseDto.setIsFeatured(bookmark.getJob().getIsFeatured());
            bookmarkResponseDto.setStatus(bookmark.getJob().getStatus().toString());
            bookmarkResponseDto.setApplicationsCount(bookmark.getJob().getApplicationsCount());
            bookmarkResponseDto.setBookmarkedAt(bookmark.getBookmarkedAt());
            bookmarkResponseDto.setNotes(bookmark.getNotes());
            bookmarkResponseDto.setCreatedAt(bookmark.getCreatedAt());
            bookmarkResponseDto.setUpdatedAt(bookmark.getUpdatedAt());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Job bookmarked successfully");
            response.put("data", bookmarkResponseDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error bookmarking job", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            
            // Provide better error message for duplicate bookmark
            if (e.getMessage().contains("already bookmarked") || e.getMessage().contains("unique_bookmark")) {
                response.put("message", "Job is already bookmarked");
            } else {
                response.put("message", "Failed to bookmark job");
            }
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Get my bookmarks", description = "Get all bookmarked jobs for the current user with search and filtering")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyBookmarks(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Show only active jobs") @RequestParam(defaultValue = "true") boolean activeOnly,
            @Parameter(description = "Search by job title") @RequestParam(required = false) String jobTitle,
            @Parameter(description = "Search by company name") @RequestParam(required = false) String companyName,
            @Parameter(description = "Job type filter") @RequestParam(required = false) com.icastar.platform.entity.Job.JobType jobType,
            @Parameter(description = "Location filter") @RequestParam(required = false) String location,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "bookmarkedAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.ARTIST_PROFILE_NOT_FOUND));

            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<BookmarkedJob> bookmarks;

            if (activeOnly) {
                bookmarks = bookmarkedJobService.findActiveBookmarksByArtist(artistProfile, pageable);
            } else {
                bookmarks = bookmarkedJobService.findByArtist(artistProfile, pageable);
            }

            // Apply search and filter if provided
            if (jobTitle != null || companyName != null || jobType != null || location != null) {
                List<BookmarkedJob> filteredBookmarks = bookmarks.getContent().stream()
                        .filter(bookmark -> {
                            boolean matchesJobTitle = jobTitle == null || 
                                    bookmark.getJob().getTitle().toLowerCase().contains(jobTitle.toLowerCase());
                            boolean matchesCompanyName = companyName == null || 
                                    (bookmark.getJob().getRecruiter() != null && 
                                     bookmark.getJob().getRecruiter().getEmail() != null &&
                                     bookmark.getJob().getRecruiter().getEmail().toLowerCase().contains(companyName.toLowerCase()));
                            boolean matchesJobType = jobType == null || 
                                    bookmark.getJob().getJobType() == jobType;
                            boolean matchesLocation = location == null || 
                                    bookmark.getJob().getLocation().toLowerCase().contains(location.toLowerCase());
                            return matchesJobTitle && matchesCompanyName && matchesJobType && matchesLocation;
                        })
                        .collect(java.util.stream.Collectors.toList());
                
                // Create a new Page with filtered content
                bookmarks = new org.springframework.data.domain.PageImpl<>(
                        filteredBookmarks, 
                        pageable, 
                        filteredBookmarks.size()
                );
            }

            // Convert entities to DTOs to avoid Jackson serialization issues
            List<BookmarkedJobDto> bookmarkDtos = bookmarks.getContent().stream()
                    .map(bookmark -> {
                        BookmarkedJobDto dto = new BookmarkedJobDto();
                        dto.setId(bookmark.getId());
                        dto.setJobId(bookmark.getJob().getId());
                        dto.setJobTitle(bookmark.getJob().getTitle());
                        dto.setJobDescription(bookmark.getJob().getDescription());
                        dto.setJobLocation(bookmark.getJob().getLocation());
                        dto.setJobType(bookmark.getJob().getJobType().toString());
                        dto.setExperienceLevel(bookmark.getJob().getExperienceLevel().toString());
                        dto.setBudgetMin(bookmark.getJob().getBudgetMin());
                        dto.setBudgetMax(bookmark.getJob().getBudgetMax());
                        dto.setCurrency(bookmark.getJob().getCurrency());
                        dto.setIsRemote(bookmark.getJob().getIsRemote());
                        dto.setIsUrgent(false);
                        dto.setIsFeatured(false);
                        dto.setStatus(bookmark.getJob().getStatus().toString());
                        dto.setApplicationsCount(bookmark.getJob().getApplicationsCount());
                        dto.setBookmarkedAt(bookmark.getBookmarkedAt());
                        dto.setNotes(bookmark.getNotes());
                        dto.setCreatedAt(bookmark.getCreatedAt());
                        dto.setUpdatedAt(bookmark.getUpdatedAt());
                        return dto;
                    })
                    .collect(java.util.stream.Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", bookmarkDtos);
            response.put("totalElements", bookmarks.getTotalElements());
            response.put("totalPages", bookmarks.getTotalPages());
            response.put("currentPage", bookmarks.getNumber());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving bookmarks", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve bookmarks");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Get bookmarks with notes", description = "Get bookmarked jobs that have personal notes")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/with-notes")
    public ResponseEntity<Map<String, Object>> getBookmarksWithNotes() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.ARTIST_PROFILE_NOT_FOUND));

            List<BookmarkedJob> bookmarks = bookmarkedJobService.findBookmarksWithNotes(artistProfile);

            // Convert entities to DTOs to avoid Jackson serialization issues
            List<BookmarkedJobDto> bookmarkDtos = bookmarks.stream()
                    .map(bookmark -> {
                        BookmarkedJobDto dto = new BookmarkedJobDto();
                        dto.setId(bookmark.getId());
                        dto.setJobId(bookmark.getJob().getId());
                        dto.setJobTitle(bookmark.getJob().getTitle());
                        dto.setJobDescription(bookmark.getJob().getDescription());
                        dto.setJobLocation(bookmark.getJob().getLocation());
                        dto.setJobType(bookmark.getJob().getJobType().toString());
                        dto.setExperienceLevel(bookmark.getJob().getExperienceLevel().toString());
                        dto.setBudgetMin(bookmark.getJob().getBudgetMin());
                        dto.setBudgetMax(bookmark.getJob().getBudgetMax());
                        dto.setCurrency(bookmark.getJob().getCurrency());
                        dto.setIsRemote(bookmark.getJob().getIsRemote());
                        dto.setIsUrgent(false);
                        dto.setIsFeatured(false);
                        dto.setStatus(bookmark.getJob().getStatus().toString());
                        dto.setApplicationsCount(bookmark.getJob().getApplicationsCount());
                        dto.setBookmarkedAt(bookmark.getBookmarkedAt());
                        dto.setNotes(bookmark.getNotes());
                        dto.setCreatedAt(bookmark.getCreatedAt());
                        dto.setUpdatedAt(bookmark.getUpdatedAt());
                        return dto;
                    })
                    .collect(java.util.stream.Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", bookmarkDtos);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving bookmarks with notes", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve bookmarks with notes");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Update bookmark", description = "Update notes for a bookmarked job")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{bookmarkId}")
    public ResponseEntity<Map<String, Object>> updateBookmark(
            @PathVariable Long bookmarkId,
            @Valid @RequestBody CreateBookmarkDto updateDto) {
        try {
            BookmarkedJob bookmark = bookmarkedJobService.updateBookmark(bookmarkId, updateDto);

            // Convert entity to DTO to avoid Jackson serialization issues
            BookmarkedJobDto bookmarkResponseDto = new BookmarkedJobDto();
            bookmarkResponseDto.setId(bookmark.getId());
            bookmarkResponseDto.setJobId(bookmark.getJob().getId());
            bookmarkResponseDto.setJobTitle(bookmark.getJob().getTitle());
            bookmarkResponseDto.setJobDescription(bookmark.getJob().getDescription());
            bookmarkResponseDto.setJobLocation(bookmark.getJob().getLocation());
            bookmarkResponseDto.setJobType(bookmark.getJob().getJobType().toString());
            bookmarkResponseDto.setExperienceLevel(bookmark.getJob().getExperienceLevel().toString());
            bookmarkResponseDto.setBudgetMin(bookmark.getJob().getBudgetMin());
            bookmarkResponseDto.setBudgetMax(bookmark.getJob().getBudgetMax());
            bookmarkResponseDto.setCurrency(bookmark.getJob().getCurrency());
            bookmarkResponseDto.setIsRemote(bookmark.getJob().getIsRemote());
            bookmarkResponseDto.setIsUrgent(bookmark.getJob().getIsUrgent());
            bookmarkResponseDto.setIsFeatured(bookmark.getJob().getIsFeatured());
            bookmarkResponseDto.setStatus(bookmark.getJob().getStatus().toString());
            bookmarkResponseDto.setApplicationsCount(bookmark.getJob().getApplicationsCount());
            bookmarkResponseDto.setBookmarkedAt(bookmark.getBookmarkedAt());
            bookmarkResponseDto.setNotes(bookmark.getNotes());
            bookmarkResponseDto.setCreatedAt(bookmark.getCreatedAt());
            bookmarkResponseDto.setUpdatedAt(bookmark.getUpdatedAt());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bookmark updated successfully");
            response.put("data", bookmarkResponseDto);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error updating bookmark", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to update bookmark");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Remove bookmark", description = "Remove a job from bookmarks")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{bookmarkId}")
    public ResponseEntity<Map<String, Object>> removeBookmark(@PathVariable Long bookmarkId) {
        try {
            bookmarkedJobService.removeBookmark(bookmarkId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bookmark removed successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error removing bookmark", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to remove bookmark");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Remove bookmark by job", description = "Remove a job from bookmarks by job ID")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/job/{jobId}")
    public ResponseEntity<Map<String, Object>> removeBookmarkByJob(@PathVariable Long jobId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.ARTIST_PROFILE_NOT_FOUND));

            bookmarkedJobService.removeBookmarkByJob(artistProfile.getId(), jobId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bookmark removed successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error removing bookmark by job", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to remove bookmark");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Check if job is bookmarked", description = "Check if a job is bookmarked by the current user")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/check/{jobId}")
    public ResponseEntity<Map<String, Object>> checkBookmarkStatus(@PathVariable Long jobId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.ARTIST_PROFILE_NOT_FOUND));

            boolean isBookmarked = bookmarkedJobService.isJobBookmarked(artistProfile.getId(), jobId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isBookmarked", isBookmarked);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking bookmark status", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to check bookmark status");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @Operation(summary = "Get bookmark statistics", description = "Get statistics about user's bookmarks")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getBookmarkStats() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.ARTIST_PROFILE_NOT_FOUND));

            Long totalBookmarks = bookmarkedJobService.countBookmarksByArtist(artistProfile);
            List<BookmarkedJob> bookmarksWithNotes = bookmarkedJobService.findBookmarksWithNotes(artistProfile);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                    "totalBookmarks", totalBookmarks,
                    "bookmarksWithNotes", bookmarksWithNotes.size()
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving bookmark stats", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve bookmark statistics");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
