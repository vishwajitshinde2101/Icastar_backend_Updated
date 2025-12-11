package com.icastar.platform.controller;

import com.icastar.platform.dto.admin.JobFilterDto;
import com.icastar.platform.dto.admin.JobManagementDto;
import com.icastar.platform.dto.admin.JobVisibilityToggleDto;
import com.icastar.platform.dto.job.JobDto;
import com.icastar.platform.dto.job.UpdateJobDto;
import com.icastar.platform.entity.Job;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.AdminJobManagementService;
import com.icastar.platform.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/jobs")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin", description = "Administrative operations and management")
@SecurityRequirement(name = "bearerAuth")
public class AdminJobManagementController {

    private final AdminJobManagementService adminJobManagementService;
    private final JobService jobService; // Added for merged methods

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllJobs(
            @RequestParam(required = false) Long recruiterId,
            @RequestParam(required = false) String recruiterName,
            @RequestParam(required = false) String recruiterEmail,
            @RequestParam(required = false) String recruiterCompany,
            @RequestParam(required = false) String recruiterCategory,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Boolean isVisible,
            @RequestParam(required = false) Long subscriptionId,
            @RequestParam(required = false) String subscriptionPlan,
            @RequestParam(required = false) String subscriptionStatus,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String createdFrom,
            @RequestParam(required = false) String createdTo,
            @RequestParam(required = false) String applicationDeadlineFrom,
            @RequestParam(required = false) String applicationDeadlineTo,
            @RequestParam(required = false) Integer minApplications,
            @RequestParam(required = false) Integer maxApplications,
            @RequestParam(required = false) Integer minViews,
            @RequestParam(required = false) Integer maxViews,
            @RequestParam(required = false) Boolean hasBoost,
            @RequestParam(required = false) Boolean isExpired,
            @RequestParam(required = false) Boolean isUrgent,
            Pageable pageable,
            Authentication authentication) {

        try {
            User admin = (User) authentication.getPrincipal();
            JobFilterDto filter = JobFilterDto.builder()
                    .recruiterId(recruiterId).recruiterName(recruiterName).recruiterEmail(recruiterEmail)
                    .recruiterCompany(recruiterCompany).recruiterCategory(recruiterCategory).status(status)
                    .isActive(isActive).isVisible(isVisible).subscriptionId(subscriptionId)
                    .subscriptionPlan(subscriptionPlan).subscriptionStatus(subscriptionStatus).jobType(jobType)
                    .location(location).title(title).minApplications(minApplications).maxApplications(maxApplications)
                    .minViews(minViews).maxViews(maxViews).hasBoost(hasBoost).isExpired(isExpired).isUrgent(isUrgent)
                    .build();

            Page<JobManagementDto> jobs = adminJobManagementService.getAllJobs(filter, pageable, admin);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", jobs.getContent());
            response.put("totalElements", jobs.getTotalElements());
            response.put("totalPages", jobs.getTotalPages());
            response.put("currentPage", jobs.getNumber());
            response.put("size", jobs.getSize());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("Failed to get jobs: " + e.getMessage());
        }
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> getJobById(@PathVariable Long jobId, Authentication authentication) {
        try {
            User admin = (User) authentication.getPrincipal();
            JobManagementDto job = adminJobManagementService.getJobById(jobId, admin);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", job);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("Failed to get job: " + e.getMessage());
        }
    }

    @PostMapping("/{jobId}/toggle-visibility")
    public ResponseEntity<Map<String, Object>> toggleJobVisibility(
            @PathVariable Long jobId,
            @Valid @RequestBody JobVisibilityToggleDto request,
            HttpServletRequest httpRequest,
            Authentication authentication) {
        try {
            User admin = (User) authentication.getPrincipal();
            request.setJobId(jobId);
            JobManagementDto result = adminJobManagementService.toggleJobVisibility(request, admin, getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"));
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Job visibility updated successfully");
            response.put("data", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("Failed to toggle job visibility: " + e.getMessage());
        }
    }

    @PostMapping("/bulk-toggle-visibility")
    public ResponseEntity<Map<String, Object>> bulkToggleJobVisibility(
            @Valid @RequestBody List<JobVisibilityToggleDto> requests,
            HttpServletRequest httpRequest,
            Authentication authentication) {
        try {
            User admin = (User) authentication.getPrincipal();
            List<JobManagementDto> results = adminJobManagementService.bulkToggleJobVisibility(requests, admin, getClientIpAddress(httpRequest), httpRequest.getHeader("User-Agent"));
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Bulk job visibility updated successfully");
            response.put("data", results);
            response.put("count", results.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("Failed to bulk toggle job visibility: " + e.getMessage());
        }
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getJobStatistics(Authentication authentication) {
        try {
            User admin = (User) authentication.getPrincipal();
            Map<String, Object> statistics = adminJobManagementService.getJobStatistics(admin);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("Failed to get job statistics: " + e.getMessage());
        }
    }

    @GetMapping("/{jobId}/logs")
    public ResponseEntity<Map<String, Object>> getJobLogs(@PathVariable Long jobId, Pageable pageable, Authentication authentication) {
        try {
            User admin = (User) authentication.getPrincipal();
            Page<Map<String, Object>> logs = adminJobManagementService.getJobLogs(jobId, pageable, admin);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", logs.getContent());
            response.put("totalElements", logs.getTotalElements());
            response.put("totalPages", logs.getTotalPages());
            response.put("currentPage", logs.getNumber());
            response.put("size", logs.getSize());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("Failed to get job logs: " + e.getMessage());
        }
    }

    @GetMapping("/recruiters")
    public ResponseEntity<Map<String, Object>> getRecruiters(Authentication authentication) {
        try {
            User admin = (User) authentication.getPrincipal();
            List<Map<String, Object>> recruiters = adminJobManagementService.getRecruiters(admin);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", recruiters);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("Failed to get recruiters: " + e.getMessage());
        }
    }

    @GetMapping("/subscription-plans")
    public ResponseEntity<Map<String, Object>> getSubscriptionPlans(Authentication authentication) {
        try {
            User admin = (User) authentication.getPrincipal();
            List<Map<String, Object>> plans = adminJobManagementService.getSubscriptionPlans(admin);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", plans);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("Failed to get subscription plans: " + e.getMessage());
        }
    }

    // Merged from AdminJobController
    @PutMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> updateJob(@PathVariable Long jobId, @RequestBody UpdateJobDto updateDto) {
        log.info("Admin updating job: {}", jobId);
        try {
            Job job = jobService.updateJobAsAdmin(jobId, updateDto);
            JobDto jobDto = new JobDto(job);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", jobDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("Failed to update job: " + e.getMessage());
        }
    }

    // Merged from AdminJobController
    @DeleteMapping("/{jobId}")
    public ResponseEntity<Map<String, Object>> deleteJob(@PathVariable Long jobId) {
        log.info("Admin deleting job: {}", jobId);
        try {
            jobService.deleteJobAsAdmin(jobId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Job deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("Failed to delete job: " + e.getMessage());
        }
    }

    // Merged from AdminJobController
    @GetMapping("/featured")
    public ResponseEntity<Map<String, Object>> getFeaturedJobs(@RequestParam(defaultValue = "10") int limit) {
        log.info("Admin fetching featured jobs with limit: {}", limit);
        try {
            List<Job> featuredJobs = jobService.getFeaturedJobs(limit);
            List<JobDto> jobDtos = featuredJobs.stream().map(JobDto::new).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", jobDtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("Failed to fetch featured jobs: " + e.getMessage());
        }
    }

    // Merged from AdminJobController
    @GetMapping("/recent")
    public ResponseEntity<Map<String, Object>> getRecentJobs(@RequestParam(defaultValue = "10") int limit) {
        log.info("Admin fetching recent jobs with limit: {}", limit);
        try {
            List<Job> recentJobs = jobService.getRecentJobs(limit);
            List<JobDto> jobDtos = recentJobs.stream().map(JobDto::new).collect(Collectors.toList());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", jobDtos);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("Failed to fetch recent jobs: " + e.getMessage());
        }
    }

    // Merged from AdminJobController
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getJobStats() {
        log.info("Admin fetching job statistics");
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalJobs", jobService.getTotalJobsCount());
            stats.put("activeJobs", jobService.getActiveJobsCount());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("Failed to fetch job statistics: " + e.getMessage());
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        return (xForwardedForHeader == null) ? request.getRemoteAddr() : xForwardedForHeader.split(",")[0];
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message) {
        log.error(message);
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }
}
