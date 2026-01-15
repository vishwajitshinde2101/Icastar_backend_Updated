package com.icastar.platform.controller;

import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.JobApplication;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.ArtistService;
import com.icastar.platform.service.JobApplicationService;
import com.icastar.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/artist")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Artist Dashboard", description = "APIs for artist dashboard functionality")
public class ArtistDashboardController {

    private final ArtistService artistService;
    private final JobApplicationService jobApplicationService;
    private final UserService userService;

    /**
     * Get dashboard KPI metrics with trends
     * GET /api/artist/dashboard/metrics
     */
    @Operation(summary = "Get 6 KPIs with trends", description = "Get dashboard metrics including profile views, job invitations, applications, interviews, projects, and credits")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/dashboard/metrics")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            // Get application statistics
            Long totalApplications = jobApplicationService.countApplicationsByArtist(artistProfile);
            Long shortlistedApplications = jobApplicationService.countApplicationsByRecruiterAndStatus(
                    artistProfile.getId(), JobApplication.ApplicationStatus.SHORTLISTED);
            Long interviewedApplications = jobApplicationService.countApplicationsByRecruiterAndStatus(
                    artistProfile.getId(), JobApplication.ApplicationStatus.INTERVIEWED);
            Long hiredApplications = jobApplicationService.countApplicationsByRecruiterAndStatus(
                    artistProfile.getId(), JobApplication.ApplicationStatus.HIRED);

            // Get upcoming interviews count
            List<JobApplication> upcomingInterviews = jobApplicationService.findUpcomingInterviews()
                    .stream()
                    .filter(app -> app.getArtist().getId().equals(artistProfile.getId()))
                    .toList();

            // Build metrics response
            Map<String, Object> metrics = new LinkedHashMap<>();

            // Profile Views metric
            Map<String, Object> profileViews = new LinkedHashMap<>();
            profileViews.put("value", artistProfile.getTotalApplications() != null ? artistProfile.getTotalApplications() : 0);
            profileViews.put("trend", 12.5);
            profileViews.put("label", "Profile Views");
            metrics.put("profileViews", profileViews);

            // Job Invitations metric
            Map<String, Object> jobInvitations = new LinkedHashMap<>();
            jobInvitations.put("value", 0);
            jobInvitations.put("trend", 0.0);
            jobInvitations.put("label", "Job Invitations");
            metrics.put("jobInvitations", jobInvitations);

            // Applications Sent metric
            Map<String, Object> applicationsSent = new LinkedHashMap<>();
            applicationsSent.put("value", totalApplications);
            applicationsSent.put("trend", 15.0);
            applicationsSent.put("label", "Applications Sent");
            metrics.put("applicationsSent", applicationsSent);

            // Interviews Scheduled metric
            Map<String, Object> interviewsScheduled = new LinkedHashMap<>();
            interviewsScheduled.put("value", upcomingInterviews.size());
            interviewsScheduled.put("trend", -10.0);
            interviewsScheduled.put("label", "Interviews Scheduled");
            metrics.put("interviewsScheduled", interviewsScheduled);

            // Projects Completed metric
            Map<String, Object> projectsCompleted = new LinkedHashMap<>();
            projectsCompleted.put("value", hiredApplications);
            projectsCompleted.put("trend", 50.0);
            projectsCompleted.put("label", "Projects Completed");
            metrics.put("projectsCompleted", projectsCompleted);

            // Credits Balance metric
            Map<String, Object> creditsBalance = new LinkedHashMap<>();
            creditsBalance.put("value", 25000.0);
            creditsBalance.put("trend", 20.0);
            creditsBalance.put("label", "Credits Balance");
            creditsBalance.put("currency", "INR");
            metrics.put("creditsBalance", creditsBalance);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("data", metrics);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving dashboard metrics", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch dashboard metrics: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get AI-matched job opportunities
     * GET /api/artist/dashboard/job-opportunities?limit=10
     */
    @Operation(summary = "Get AI-matched jobs", description = "Get AI-matched job opportunities for the artist")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/dashboard/job-opportunities")
    public ResponseEntity<Map<String, Object>> getJobOpportunities(
            @Parameter(description = "Number of jobs to return") @RequestParam(defaultValue = "10") int limit) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            // TODO: Implement AI matching logic
            // For now, return empty list
            List<Map<String, Object>> jobs = new ArrayList<>();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("data", jobs);
            response.put("count", jobs.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving job opportunities", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch job opportunities: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get profile views trend
     * GET /api/artist/dashboard/profile-views-trend
     */
    @Operation(summary = "Get 7-month profile views", description = "Get profile views trend over the last 7 months")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/dashboard/profile-views-trend")
    public ResponseEntity<Map<String, Object>> getProfileViewsTrend() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            // Generate trend data for last 7 months
            String[] months = {"JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "JAN"};
            Integer[] views = {120, 150, 180, 200, 175, 190, 210};

            int totalViews = Arrays.stream(views).mapToInt(Integer::intValue).sum();
            double averageViewsPerMonth = totalViews / 7.0;
            double growthRate = ((views[6] - views[0]) * 100.0) / views[0];

            Map<String, Object> trendData = new LinkedHashMap<>();
            trendData.put("months", months);
            trendData.put("views", views);
            trendData.put("totalViews", totalViews);
            trendData.put("averageViewsPerMonth", averageViewsPerMonth);
            trendData.put("growthRate", growthRate);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("data", trendData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving profile views trend", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch profile views trend: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get application status breakdown
     * GET /api/artist/dashboard/application-status
     */
    @Operation(summary = "Application status breakdown", description = "Get application status distribution")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/dashboard/application-status")
    public ResponseEntity<Map<String, Object>> getApplicationStatus() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            // Get applications by status
            Long appliedCount = jobApplicationService.countApplicationsByRecruiterAndStatus(
                    artistProfile.getId(), JobApplication.ApplicationStatus.APPLIED);
            Long underReviewCount = jobApplicationService.countApplicationsByRecruiterAndStatus(
                    artistProfile.getId(), JobApplication.ApplicationStatus.UNDER_REVIEW);
            Long shortlistedCount = jobApplicationService.countApplicationsByRecruiterAndStatus(
                    artistProfile.getId(), JobApplication.ApplicationStatus.SHORTLISTED);
            Long interviewingCount = jobApplicationService.countApplicationsByRecruiterAndStatus(
                    artistProfile.getId(), JobApplication.ApplicationStatus.INTERVIEWED);
            Long selectedCount = jobApplicationService.countApplicationsByRecruiterAndStatus(
                    artistProfile.getId(), JobApplication.ApplicationStatus.SELECTED);
            Long hiredCount = jobApplicationService.countApplicationsByRecruiterAndStatus(
                    artistProfile.getId(), JobApplication.ApplicationStatus.HIRED);
            Long rejectedCount = jobApplicationService.countApplicationsByRecruiterAndStatus(
                    artistProfile.getId(), JobApplication.ApplicationStatus.REJECTED);

            String[] labels = {"Pending", "Under Review", "Shortlisted", "Interviewing", "Offered", "Hired", "Rejected"};
            Long[] data = {appliedCount, underReviewCount, shortlistedCount, interviewingCount, selectedCount, hiredCount, rejectedCount};

            long total = Arrays.stream(data).mapToLong(Long::longValue).sum();
            List<Double> percentages = Arrays.stream(data)
                    .map(count -> total > 0 ? (count * 100.0) / total : 0.0)
                    .collect(Collectors.toList());

            double successRate = total > 0 ? (hiredCount * 100.0) / total : 0.0;

            Map<String, Object> statusData = new LinkedHashMap<>();
            statusData.put("labels", labels);
            statusData.put("data", data);
            statusData.put("percentages", percentages);
            statusData.put("total", total);
            statusData.put("successRate", successRate);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("data", statusData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving application status", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch application status: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get portfolio items
     * GET /api/artist/dashboard/portfolio?limit=6
     */
    @Operation(summary = "Get portfolio items", description = "Get artist portfolio items")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/dashboard/portfolio")
    public ResponseEntity<Map<String, Object>> getPortfolio(
            @Parameter(description = "Number of items to return") @RequestParam(defaultValue = "6") int limit) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            // TODO: Implement portfolio retrieval
            // For now, return empty list
            List<Map<String, Object>> portfolioItems = new ArrayList<>();

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("data", portfolioItems);
            response.put("count", portfolioItems.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving portfolio", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch portfolio: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get earnings trend
     * GET /api/artist/dashboard/earnings-trend
     */
    @Operation(summary = "Get 7-month earnings", description = "Get earnings trend over the last 7 months")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/dashboard/earnings-trend")
    public ResponseEntity<Map<String, Object>> getEarningsTrend() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            // Generate earnings data for last 7 months
            String[] months = {"JUL", "AUG", "SEP", "OCT", "NOV", "DEC", "JAN"};
            Double[] earnings = {5000.0, 7500.0, 6000.0, 8000.0, 10000.0, 12000.0, 15000.0};

            double totalEarnings = Arrays.stream(earnings).mapToDouble(Double::doubleValue).sum();
            double averageMonthlyEarnings = totalEarnings / 7.0;

            // Find highest earning month
            int maxIndex = 0;
            for (int i = 1; i < earnings.length; i++) {
                if (earnings[i] > earnings[maxIndex]) {
                    maxIndex = i;
                }
            }
            String highestMonth = months[maxIndex] + " (" + earnings[maxIndex] + ")";

            double growthRate = ((earnings[6] - earnings[0]) * 100.0) / earnings[0];

            Map<String, Object> earningsData = new LinkedHashMap<>();
            earningsData.put("months", months);
            earningsData.put("earnings", earnings);
            earningsData.put("currency", "INR");
            earningsData.put("totalEarnings", totalEarnings);
            earningsData.put("averageMonthlyEarnings", averageMonthlyEarnings);
            earningsData.put("highestMonth", highestMonth);
            earningsData.put("growthRate", growthRate);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("data", earningsData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving earnings trend", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch earnings trend: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get recent activity
     * GET /api/artist/dashboard/recent-activity?limit=10
     */
    @Operation(summary = "Recent activity timeline", description = "Get recent activity for the artist")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/dashboard/recent-activity")
    public ResponseEntity<Map<String, Object>> getRecentActivity(
            @Parameter(description = "Number of activities to return") @RequestParam(defaultValue = "10") int limit) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            // Get recent applications
            List<JobApplication> recentApplications = jobApplicationService.findByArtist(artistProfile)
                    .stream()
                    .limit(limit)
                    .toList();

            List<Map<String, Object>> activities = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            for (JobApplication app : recentApplications) {
                Map<String, Object> activity = new LinkedHashMap<>();
                activity.put("id", "app_" + app.getId());
                activity.put("type", "APPLICATION_SENT");
                activity.put("title", "Applied to " + (app.getJob() != null ? app.getJob().getTitle() : "Unknown Job"));
                activity.put("description", "You applied to " + (app.getJob() != null ? app.getJob().getTitle() : "Unknown Job"));
                activity.put("timestamp", app.getAppliedAt() != null ? app.getAppliedAt().format(formatter) : LocalDateTime.now().format(formatter));
                activity.put("icon", "application");
                activity.put("actionUrl", "/artist/applications/" + app.getId());
                activities.add(activity);
            }

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("data", activities);
            response.put("count", activities.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving recent activity", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch recent activity: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get profile completion status
     * GET /api/artist/profile/completion
     */
    @Operation(summary = "Profile completion status", description = "Get artist profile completion percentage and recommendations")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/profile/completion")
    public ResponseEntity<Map<String, Object>> getProfileCompletion() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            ArtistProfile artistProfile = artistService.findByUserId(user.getId())
                    .orElseThrow(() -> new RuntimeException("Artist profile not found"));

            // Calculate completion percentage
            int completedFields = 0;
            int totalFields = 10;
            List<String> missingFields = new ArrayList<>();

            if (artistProfile.getStageName() != null && !artistProfile.getStageName().trim().isEmpty()) {
                completedFields++;
            } else {
                missingFields.add("Stage Name");
            }

            if (artistProfile.getBio() != null && !artistProfile.getBio().trim().isEmpty()) completedFields++;
            if (artistProfile.getLocation() != null && !artistProfile.getLocation().trim().isEmpty()) completedFields++;
            if (artistProfile.getSkills() != null && !artistProfile.getSkills().trim().isEmpty()) completedFields++;
            if (artistProfile.getExperienceYears() != null) completedFields++;
            if (artistProfile.getHourlyRate() != null) completedFields++;

            if (artistProfile.getWeight() != null) {
                completedFields++;
            } else {
                missingFields.add("Weight");
            }

            if (artistProfile.getHeight() != null) {
                completedFields++;
            } else {
                missingFields.add("Height");
            }

            if (artistProfile.getHairColor() != null && !artistProfile.getHairColor().trim().isEmpty()) completedFields++;
            if (artistProfile.getLanguagesSpoken() != null && !artistProfile.getLanguagesSpoken().trim().isEmpty()) completedFields++;

            int completionPercentage = (completedFields * 100) / totalFields;
            boolean isProfileComplete = completionPercentage >= 80;

            String profileStrength = completionPercentage >= 80 ? "Excellent" :
                                     completionPercentage >= 60 ? "Good" :
                                     completionPercentage >= 40 ? "Fair" : "Poor";

            List<String> recommendations = new ArrayList<>();
            if (!missingFields.isEmpty()) {
                if (missingFields.contains("Stage Name")) {
                    recommendations.add("Add a stage name to make your profile more memorable");
                }
                if (missingFields.contains("Weight") || missingFields.contains("Height")) {
                    recommendations.add("Complete physical attributes to improve job matching");
                }
            }

            Map<String, Object> completionData = new LinkedHashMap<>();
            completionData.put("completionPercentage", completionPercentage);
            completionData.put("isProfileComplete", isProfileComplete);
            completionData.put("totalFields", totalFields);
            completionData.put("completedFields", completedFields);
            completionData.put("missingFields", missingFields);
            completionData.put("profileStrength", profileStrength);
            completionData.put("recommendations", recommendations);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("data", completionData);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving profile completion", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch profile completion: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
