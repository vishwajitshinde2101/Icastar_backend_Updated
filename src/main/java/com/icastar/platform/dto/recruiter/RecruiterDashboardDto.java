package com.icastar.platform.dto.recruiter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecruiterDashboardDto {
    
    // Recruiter Information
    private Long recruiterId;
    private String recruiterName;
    private String companyName;
    private String recruiterCategory;
    private String email;
    private String phone;
    private Boolean isActive;
    
    // Subscription Information
    private Long subscriptionId;
    private String subscriptionPlan;
    private String subscriptionStatus;
    private LocalDateTime subscriptionExpiresAt;
    
    // Dashboard Statistics
    private Long totalJobsPosted;
    private Long activeJobs;
    private Long closedJobs;
    private Long totalApplications;
    private Long totalHires;
    private Long totalViews;
    private Double averageApplicationsPerJob;
    private Double hireRate;
    
    // Recent Activity
    private List<RecentJobDto> recentJobs;
    private List<RecentApplicationDto> recentApplications;
    private List<RecentHireDto> recentHires;
    
    // Quick Actions Available
    private Boolean canPostNewJob;
    private Boolean canViewApplications;
    private Boolean canBrowseArtists;
    private Boolean canGetSuggestions;
    private Boolean canTrackHires;
    
    // Subscription Features
    private List<String> availableFeatures;
    private List<String> premiumFeatures;
    private Boolean hasPremiumFeatures;
}
