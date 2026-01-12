package com.icastar.platform.controller;

import com.icastar.platform.dto.job.JobApplicationDto;
import com.icastar.platform.entity.JobApplication;
import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.JobApplicationService;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/my-applications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "My Applications", description = "APIs for viewing current user's job applications")
public class MyApplicationsController {

    private final JobApplicationService jobApplicationService;
    private final ArtistService artistService;
    private final UserService userService;

    @Operation(summary = "Get my applications", description = "Get all applications for the current user with search and filtering")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMyApplications(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Status filter") @RequestParam(required = false) JobApplication.ApplicationStatus status,
            @Parameter(description = "Search by job title") @RequestParam(required = false) String jobTitle,
            @Parameter(description = "Search by company name") @RequestParam(required = false) String companyName,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "appliedAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            log.info("Fetching applications for user: {}", email);

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            log.info("Found user with ID: {}", user.getId());

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));
            log.info("Found artist profile with ID: {}", artistProfile.getId());

            Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<JobApplication> applications;

            if (status != null) {
                log.info("Filtering by status: {}", status);
                applications = jobApplicationService.findByArtistAndStatus(artistProfile, status, pageable);
            } else {
                applications = jobApplicationService.findByArtist(artistProfile, pageable);
            }
            log.info("Found {} applications for artist ID: {}", applications.getTotalElements(), artistProfile.getId());

            // Apply search filters if provided
            if (jobTitle != null || companyName != null) {
                List<JobApplication> filteredApplications = applications.getContent().stream()
                        .filter(application -> {
                            boolean matchesJobTitle = jobTitle == null ||
                                    application.getJob().getTitle().toLowerCase().contains(jobTitle.toLowerCase());
                            boolean matchesCompanyName = companyName == null ||
                                    (application.getJob().getRecruiter() != null &&
                                     application.getJob().getRecruiter().getRecruiterProfile() != null &&
                                     application.getJob().getRecruiter().getRecruiterProfile().getCompanyName() != null &&
                                     application.getJob().getRecruiter().getRecruiterProfile().getCompanyName().toLowerCase().contains(companyName.toLowerCase()));
                            return matchesJobTitle && matchesCompanyName;
                        })
                        .collect(java.util.stream.Collectors.toList());

                // Create a new Page with filtered content
                applications = new org.springframework.data.domain.PageImpl<>(
                        filteredApplications,
                        pageable,
                        filteredApplications.size()
                );
            }

            // Convert entities to DTOs to avoid Jackson serialization issues
            List<JobApplicationDto> applicationDtos = applications.getContent().stream()
                    .map(application -> {
                        JobApplicationDto dto = new JobApplicationDto();
                        dto.setId(application.getId());
                        dto.setJobId(application.getJob().getId());
                        dto.setJobTitle(application.getJob().getTitle());
                        dto.setArtistId(application.getArtist().getId());

                        // Safely handle artist name
                        String firstName = application.getArtist().getFirstName() != null ? application.getArtist().getFirstName() : "";
                        String lastName = application.getArtist().getLastName() != null ? application.getArtist().getLastName() : "";
                        dto.setArtistName((firstName + " " + lastName).trim());

                        // Safely handle artist email
                        if (application.getArtist().getUser() != null) {
                            dto.setArtistEmail(application.getArtist().getUser().getEmail());
                        }

                        dto.setStatus(application.getStatus());
                        dto.setCoverLetter(application.getCoverLetter());
                        dto.setExpectedSalary(application.getExpectedSalary());
                        dto.setAvailabilityDate(application.getAvailabilityDate());
                        dto.setPortfolioUrl(application.getPortfolioUrl());
                        dto.setResumeUrl(application.getResumeUrl());
                        dto.setDemoReelUrl(application.getDemoReelUrl());
                        dto.setAppliedAt(application.getAppliedAt());
                        dto.setReviewedAt(application.getReviewedAt());
                        dto.setInterviewScheduledAt(application.getInterviewScheduledAt());
                        dto.setInterviewNotes(application.getInterviewNotes());
                        dto.setRejectionReason(application.getRejectionReason());
                        dto.setFeedback(application.getFeedback());
                        dto.setRating(application.getRating());
                        dto.setIsShortlisted(application.getIsShortlisted());
                        dto.setIsHired(application.getIsHired());
                        dto.setHiredAt(application.getHiredAt());
                        dto.setContractUrl(application.getContractUrl());
                        dto.setNotes(application.getNotes());
                        dto.setCreatedAt(application.getCreatedAt());
                        dto.setUpdatedAt(application.getUpdatedAt());
                        return dto;
                    })
                    .collect(java.util.stream.Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", applicationDtos);
            response.put("totalElements", applications.getTotalElements());
            response.put("totalPages", applications.getTotalPages());
            response.put("currentPage", applications.getNumber());

            log.info("Returning {} applications to user", applicationDtos.size());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("Error retrieving applications for user", e);

            // Provide more specific error messages
            String errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.contains("User not found")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "User account not found");
                response.put("error", errorMessage);
                return ResponseEntity.badRequest().body(response);
            } else if (errorMessage != null && errorMessage.contains("Artist profile not found")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Artist profile not found. Please complete your artist profile first.");
                response.put("error", errorMessage);
                return ResponseEntity.badRequest().body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retrieve applications");
            response.put("error", errorMessage);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Unexpected error retrieving applications", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "An unexpected error occurred");
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
