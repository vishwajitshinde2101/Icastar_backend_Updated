package com.icastar.platform.controller;

import com.icastar.platform.dto.application.JobApplicationDto;
import com.icastar.platform.dto.application.UpdateApplicationStatusDto;
import com.icastar.platform.entity.JobApplication;
import com.icastar.platform.service.JobApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/applications")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminApplicationController {

    private final JobApplicationService jobApplicationService;

    @GetMapping
    public ResponseEntity<Page<JobApplicationDto>> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "appliedAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) JobApplication.ApplicationStatus status,
            @RequestParam(required = false) Long jobId,
            @RequestParam(required = false) Long artistId,
            @RequestParam(required = false) Long recruiterId) {

        log.info("Admin fetching applications - page: {}, size: {}, sortBy: {}, sortDir: {}, status: {}, jobId: {}, artistId: {}, recruiterId: {}", 
                page, size, sortBy, sortDir, status, jobId, artistId, recruiterId);

        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<JobApplication> applications;

        if (jobId != null) {
            applications = jobApplicationService.findByJob(jobId, pageable);
        } else if (artistId != null) {
            applications = jobApplicationService.findByArtist(artistId, pageable);
        } else if (recruiterId != null) {
            applications = jobApplicationService.findByRecruiter(recruiterId, pageable);
        } else {
            // Get all applications - we need to implement this method
            applications = jobApplicationService.findAll(pageable);
        }

        Page<JobApplicationDto> applicationDtos = applications.map(JobApplicationDto::new);
        
        return ResponseEntity.ok(applicationDtos);
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<JobApplicationDto> getApplication(@PathVariable Long applicationId) {
        log.info("Admin fetching application details for application ID: {}", applicationId);
        
        JobApplication application = jobApplicationService.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found with id: " + applicationId));
        
        JobApplicationDto applicationDto = new JobApplicationDto(application);
        
        return ResponseEntity.ok(applicationDto);
    }

    @PutMapping("/{applicationId}/status")
    public ResponseEntity<JobApplicationDto> updateApplicationStatus(
            @PathVariable Long applicationId,
            @RequestBody UpdateApplicationStatusDto updateDto) {
        
        log.info("Admin updating application status for application: {}", applicationId);
        
        // Admin can update any application, so we pass null as recruiterId
        JobApplication application = jobApplicationService.updateApplicationStatus(
            applicationId, null, updateDto);
        JobApplicationDto applicationDto = new JobApplicationDto(application);
        
        return ResponseEntity.ok(applicationDto);
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Map<String, String>> deleteApplication(@PathVariable Long applicationId) {
        log.info("Admin deleting application: {}", applicationId);
        
        // Admin can delete any application
        jobApplicationService.deleteApplication(applicationId, null);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Application deleted successfully");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getApplicationStats() {
        log.info("Admin fetching application statistics");
        
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalApplications", jobApplicationService.getTotalApplicationsCount());
        stats.put("pendingApplications", jobApplicationService.getApplicationsCountByStatus(JobApplication.ApplicationStatus.UNDER_REVIEW));
        stats.put("acceptedApplications", jobApplicationService.getApplicationsCountByStatus(JobApplication.ApplicationStatus.SELECTED));
        stats.put("rejectedApplications", jobApplicationService.getApplicationsCountByStatus(JobApplication.ApplicationStatus.REJECTED));
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/recent")
    public ResponseEntity<List<JobApplicationDto>> getRecentApplications(
            @RequestParam(defaultValue = "10") int limit) {
        
        log.info("Admin fetching recent applications with limit: {}", limit);
        
        List<JobApplication> recentApplications = jobApplicationService.getRecentApplications(limit);
        List<JobApplicationDto> applicationDtos = recentApplications.stream()
                .map(JobApplicationDto::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(applicationDtos);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<JobApplicationDto>> getPendingApplications() {
        log.info("Admin fetching pending applications");
        
        List<JobApplication> pendingApplications = jobApplicationService.findByJobAndStatus(
            null, JobApplication.ApplicationStatus.UNDER_REVIEW);
        
        List<JobApplicationDto> applicationDtos = pendingApplications.stream()
                .map(JobApplicationDto::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(applicationDtos);
    }
}
