package com.icastar.platform.controller;

import com.icastar.platform.dto.application.JobApplicationDto;
import com.icastar.platform.dto.application.UpdateApplicationStatusDto;
import com.icastar.platform.entity.JobApplication;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.JobApplicationService;
import com.icastar.platform.service.UserService;
import jakarta.validation.Valid;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/recruiter/applications")
@RequiredArgsConstructor
@Slf4j
public class RecruiterApplicationController {

    private final JobApplicationService jobApplicationService;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<JobApplicationDto>> getApplicationsForMyJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "appliedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) JobApplication.ApplicationStatus status) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User recruiter = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (recruiter.getRole() != User.UserRole.RECRUITER) {
                throw new RuntimeException("Only recruiters can view applications");
            }

            log.info("Fetching applications for recruiter: {}", email);

            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<JobApplication> applications = jobApplicationService.findByRecruiter(recruiter.getId(), pageable);

            // Filter by status if provided
            if (status != null) {
                applications = applications.map(app -> 
                    app.getStatus() == status ? app : null
                ).map(app -> app != null ? app : null);
            }

            Page<JobApplicationDto> applicationDtos = applications.map(JobApplicationDto::new);

            return ResponseEntity.ok(applicationDtos);
        } catch (Exception e) {
            log.error("Error fetching applications", e);
            throw new RuntimeException("Failed to fetch applications: " + e.getMessage());
        }
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<Page<JobApplicationDto>> getApplicationsForJob(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "appliedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) JobApplication.ApplicationStatus status) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User recruiter = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (recruiter.getRole() != User.UserRole.RECRUITER) {
                throw new RuntimeException("Only recruiters can view applications");
            }

            log.info("Fetching applications for job {} by recruiter: {}", jobId, email);

            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<JobApplication> applications = jobApplicationService.findByJobId(jobId, pageable);

            // Filter by status if provided
            if (status != null) {
                applications = applications.map(app -> 
                    app.getStatus() == status ? app : null
                ).map(app -> app != null ? app : null);
            }

            Page<JobApplicationDto> applicationDtos = applications.map(JobApplicationDto::new);

            return ResponseEntity.ok(applicationDtos);
        } catch (Exception e) {
            log.error("Error fetching applications for job", e);
            throw new RuntimeException("Failed to fetch applications for job: " + e.getMessage());
        }
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<JobApplicationDto> getApplication(@PathVariable Long applicationId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User recruiter = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (recruiter.getRole() != User.UserRole.RECRUITER) {
                throw new RuntimeException("Only recruiters can view applications");
            }

            log.info("Fetching application {} for recruiter: {}", applicationId, email);

            JobApplication application = jobApplicationService.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found with id: " + applicationId));

            // Verify that the recruiter owns the job
            if (!application.getJob().getRecruiter().getId().equals(recruiter.getId())) {
                throw new RuntimeException("You don't have permission to view this application");
            }

            JobApplicationDto applicationDto = new JobApplicationDto(application);

            return ResponseEntity.ok(applicationDto);
        } catch (Exception e) {
            log.error("Error fetching application", e);
            throw new RuntimeException("Failed to fetch application: " + e.getMessage());
        }
    }

    @PutMapping("/{applicationId}/status")
    public ResponseEntity<JobApplicationDto> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusDto updateDto) {
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User recruiter = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (recruiter.getRole() != User.UserRole.RECRUITER) {
                throw new RuntimeException("Only recruiters can update application status");
            }

            log.info("Updating application status for application {} by recruiter: {}", applicationId, email);

            JobApplication application = jobApplicationService.updateApplicationStatus(
                applicationId, recruiter.getId(), updateDto);
            JobApplicationDto applicationDto = new JobApplicationDto(application);

            return ResponseEntity.ok(applicationDto);
        } catch (Exception e) {
            log.error("Error updating application status", e);
            throw new RuntimeException("Failed to update application status: " + e.getMessage());
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<JobApplicationDto>> getPendingApplications() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User recruiter = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (recruiter.getRole() != User.UserRole.RECRUITER) {
                throw new RuntimeException("Only recruiters can view pending applications");
            }

            log.info("Fetching pending applications for recruiter: {}", email);

            List<JobApplication> pendingApplications = jobApplicationService.findByRecruiterAndStatus(
                recruiter.getId(), JobApplication.ApplicationStatus.UNDER_REVIEW);
            
            List<JobApplicationDto> applicationDtos = pendingApplications.stream()
                    .map(JobApplicationDto::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(applicationDtos);
        } catch (Exception e) {
            log.error("Error fetching pending applications", e);
            throw new RuntimeException("Failed to fetch pending applications: " + e.getMessage());
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getApplicationStats() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User recruiter = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (recruiter.getRole() != User.UserRole.RECRUITER) {
                throw new RuntimeException("Only recruiters can view application stats");
            }

            log.info("Fetching application stats for recruiter: {}", email);

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalApplications", jobApplicationService.getApplicationsCountByRecruiter(recruiter.getId()));
            stats.put("pendingApplications", jobApplicationService.getApplicationsCountByStatus(JobApplication.ApplicationStatus.UNDER_REVIEW));
            stats.put("acceptedApplications", jobApplicationService.getApplicationsCountByStatus(JobApplication.ApplicationStatus.SELECTED));
            stats.put("rejectedApplications", jobApplicationService.getApplicationsCountByStatus(JobApplication.ApplicationStatus.REJECTED));

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error fetching application stats", e);
            throw new RuntimeException("Failed to fetch application stats: " + e.getMessage());
        }
    }
}
