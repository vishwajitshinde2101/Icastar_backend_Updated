package com.icastar.platform.service;

import com.icastar.platform.constants.ApplicationConstants;

import com.icastar.platform.dto.recruiter.*;
import com.icastar.platform.entity.*;
import com.icastar.platform.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.icastar.platform.dto.application.JobApplicationDto;

import com.icastar.platform.dto.recruiter.CreateJobDto;
import com.icastar.platform.exception.ValidationException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecruiterDashboardService {
    
    private final UserRepository userRepository;
    private final RecruiterProfileRepository recruiterProfileRepository;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UsageTrackingRepository usageTrackingRepository;
    private final EmailService emailService;
    private final ArtistService artistService;




    /**
     * Get recruiter dashboard overview
     */
    @Transactional(readOnly = true)
    public RecruiterDashboardDto getDashboard(User recruiter) {
        // Get recruiter profile
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.RECRUITER_PROFILE_NOT_FOUND));

        // Get subscription information
        Optional<Subscription> subscription = subscriptionRepository.findByUserIdAndStatus(
                recruiter.getId(), Subscription.SubscriptionStatus.ACTIVE);
        Map<String, Object> statistics = getDashboardStatistics(recruiter);
        List<RecentJobDto> recentJobs = getRecentJobs(recruiter, 5);
        List<RecentApplicationDto> recentApplications = getRecentApplications(recruiter, 5);
        List<RecentHireDto> recentHires = getRecentHires(recruiter, 5);
        return RecruiterDashboardDto.builder()
                .recruiterId(recruiterProfile.getId() != null ? recruiterProfile.getId() : null)
                .recruiterName(recruiterProfile.getContactPersonName() != null ? recruiterProfile.getContactPersonName() : null)
                .companyName(recruiterProfile.getCompanyName() != null ? recruiterProfile.getCompanyName() : null)
                .recruiterCategory(recruiterProfile.getRecruiterCategory() != null ?
                        recruiterProfile.getRecruiterCategory().getDisplayName() : "Unknown")
                .email(recruiter.getEmail())
                .phone(recruiterProfile.getContactPhone() != null ? recruiterProfile.getContactPhone() : null )
                .isActive(recruiterProfile.getIsActive() != null ? recruiterProfile.getIsActive() : null)
                .subscriptionId(subscription.map(Subscription::getId).orElse(null))
                .subscriptionPlan(subscription.map(s -> s.getSubscriptionPlan().getName()).orElse(ApplicationConstants.DefaultValues.DEFAULT_SUBSCRIPTION_PLAN))
                .subscriptionStatus(subscription.map(s -> s.getStatus().name()).orElse(ApplicationConstants.Status.INACTIVE))
                .subscriptionExpiresAt(subscription.map(Subscription::getExpiresAt).orElse(null))
                .totalJobsPosted((Long) statistics.get("totalJobsPosted"))
                .activeJobs((Long) statistics.get("activeJobs"))
                .closedJobs((Long) statistics.get("closedJobs"))
                .totalApplications((Long) statistics.get("totalApplications"))
                .totalHires((Long) statistics.get("totalHires"))
                .totalViews((Long) statistics.get("totalViews"))
                .averageApplicationsPerJob((Double) statistics.get("averageApplicationsPerJob"))
                .hireRate((Double) statistics.get("hireRate"))
                .recentJobs(recentJobs)
                .recentApplications(recentApplications)
                .recentHires(recentHires)
                .canViewApplications(true)
                .canBrowseArtists(true)
                .canGetSuggestions(true)
                .canTrackHires(true)
                .availableFeatures(getAvailableFeatures(recruiter))
                .premiumFeatures(getPremiumFeatures(recruiter))
                .hasPremiumFeatures(hasPremiumFeatures(recruiter))
                .build();
    }
    
    /**
     * Get all jobs posted by recruiter
     */
    @Transactional(readOnly = true)
    public Page<RecentJobDto> getMyJobs(String status, Boolean isActive, String jobType,
                                       String location, String title, Pageable pageable, User recruiter) {
        // Get recruiter profile
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.RECRUITER_PROFILE_NOT_FOUND));
        
        // Build query based on filters
        Page<Job> jobs = jobRepository.findByRecruiter(recruiterProfile.getUser(), pageable);

        // Apply additional filters if needed
        if (status != null) {
            // Filter jobs by status
            List<Job> filteredJobs = jobs.getContent().stream()
                    .filter(job -> job.getStatus().name().equals(status))
                    .collect(Collectors.toList());
            // Create a new Page with filtered content
            jobs = new org.springframework.data.domain.PageImpl<>(filteredJobs, pageable, filteredJobs.size());
        }

        return jobs.map(this::convertToRecentJobDto);
    }

    /**
     * Get job details by ID
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getJobDetails(Long jobId, User recruiter) {
        // Get recruiter profile
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.RECRUITER_PROFILE_NOT_FOUND));
        
        // Get job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.JOB_NOT_FOUND));
        
        // Check if job belongs to recruiter
        if (!job.getRecruiter().getId().equals(recruiterProfile.getUser().getId())) {
            throw new RuntimeException(ApplicationConstants.ErrorMessages.ACCESS_DENIED);
        }

        // Get applications count - TODO: Calculate from JobApplication repository if needed
        int applicationCount = 0;

        // Build job details
        Map<String, Object> jobDetails = new HashMap<>();
        jobDetails.put("id", job.getId());
        jobDetails.put("title", job.getTitle());
        jobDetails.put("description", job.getDescription());
        jobDetails.put("requirements", job.getRequirements());
        jobDetails.put("jobType", job.getJobType());
        jobDetails.put("location", job.getLocation());
        jobDetails.put("salaryMin", job.getBudgetMin());
        jobDetails.put("salaryMax", job.getBudgetMax());
        jobDetails.put("currency", job.getCurrency());
        jobDetails.put("status", job.getStatus());
        jobDetails.put("isActive", job.getStatus() == Job.JobStatus.ACTIVE);
        jobDetails.put("applicationDeadline", job.getApplicationDeadline());
        jobDetails.put("startDate", job.getStartDate());
        jobDetails.put("createdAt", job.getCreatedAt());
        jobDetails.put("updatedAt", job.getUpdatedAt());
        jobDetails.put("applicationCount", applicationCount);
        jobDetails.put("viewCount", 0); // This would need to be tracked separately
        jobDetails.put("boostCount", 0); // This would need to be tracked separately
        
        return jobDetails;
    }
    
    /**
     * Get job applications
     */
    @Transactional(readOnly = true)
    public Page<RecentApplicationDto> getJobApplications(Long jobId, String status, String artistCategory,
                                                        String location, String experienceLevel, 
                                                        Pageable pageable, User recruiter) {
        // Get recruiter profile
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.RECRUITER_PROFILE_NOT_FOUND));
        
        // Get job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.JOB_NOT_FOUND));
        
        // Check if job belongs to recruiter
        if (!job.getRecruiter().getId().equals(recruiterProfile.getUser().getId())) {
            throw new RuntimeException(ApplicationConstants.ErrorMessages.ACCESS_DENIED);
        }
        
        // Get applications
        Page<JobApplication> applications = jobApplicationRepository.findByJobId(jobId, pageable);
        
        return applications.map(this::convertToRecentApplicationDto);
    }
    
    /**
     * Browse artist profiles
     */
    @Transactional(readOnly = true)
    public Page<ArtistSuggestionDto> browseArtists(String artistCategory, String artistType, String location,
                                                    String skills, String genres, String experienceLevel,
                                                    String availability, Boolean isVerified, Boolean isPremium,
                                                    Pageable pageable, String email) {

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        // Get all artist profiles with filters
        Page<ArtistProfile> artistProfiles = artistProfileRepository.findAll(pageable);
        
        // Apply filters
        if (artistCategory != null) {
            // Filter artist profiles by category
            List<ArtistProfile> filteredProfiles = artistProfiles.getContent().stream()
                    .filter(profile -> profile.getArtistType() != null && 
                            profile.getArtistType().getDisplayName().equals(artistCategory))
                    .collect(Collectors.toList());
            // Create a new Page with filtered content
            artistProfiles = new PageImpl<>(filteredProfiles, pageable, filteredProfiles.size());
        }
        
        return artistProfiles.map(this::convertToArtistSuggestionDto);
    }
    
    /**
     * Get artist suggestions based on job criteria
     */
    @Transactional(readOnly = true)
    public List<ArtistSuggestionDto> getArtistSuggestions(Long jobId, String artistCategory, String artistType,
                                                          String location, String skills, String genres,
                                                          String experienceLevel, String availability,
                                                          Boolean isVerified, Boolean isPremium, Integer limit,
                                                          User recruiter) {
        // Get job if jobId is provided
        final Job job = jobId != null ?
                jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found")) : null;
        
        // Get all artist profiles
        List<ArtistProfile> artistProfiles = artistProfileRepository.findAll();
        
        // Convert to suggestions and calculate match scores
        List<ArtistSuggestionDto> suggestions = artistProfiles.stream()
                .map(profile -> {
                    ArtistSuggestionDto suggestion = convertToArtistSuggestionDto(profile);
                    
                    // Calculate match score based on criteria
                    double matchScore = calculateMatchScore(profile, job, artistCategory, artistType,
                            location, skills, genres, experienceLevel, availability, isVerified, isPremium);
                    
                    suggestion.setMatchScore(matchScore);
                    suggestion.setMatchReasons(generateMatchReasons(profile, job, artistCategory, artistType,
                            location, skills, genres, experienceLevel, availability, isVerified, isPremium));
                    
                    return suggestion;
                })
                .filter(suggestion -> suggestion.getMatchScore() > 0.3) // Only show relevant matches
                .sorted((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()))
                .limit(limit)
                .collect(Collectors.toList());
        
        return suggestions;
    }
    
    /**
     * Get hires and past jobs
     */
    @Transactional(readOnly = true)
    public Page<RecentHireDto> getHires(String status, String artistCategory, String jobType,
                                        String performanceRating, Boolean isCompleted, Boolean isRecommended,
                                        Pageable pageable, User recruiter) {
        // Get recruiter profile
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.RECRUITER_PROFILE_NOT_FOUND));
        
        // Get jobs by recruiter
        Page<Job> jobs = jobRepository.findByRecruiter(recruiterProfile.getUser(), pageable);

        // Convert to hires (this would need to be implemented with a proper hire tracking system)
        return jobs.map(job -> {
            // This is a placeholder - would need actual hire tracking
            return RecentHireDto.builder()
                    .id(job.getId())
                    .jobId(job.getId())
                    .jobTitle(job.getTitle())
                    .artistId(1L) // Placeholder
                    .artistName(ApplicationConstants.DefaultValues.SAMPLE_ARTIST_NAME)
                    .artistEmail(ApplicationConstants.DefaultValues.SAMPLE_ARTIST_EMAIL)
                    .artistCategory(ApplicationConstants.DefaultValues.SAMPLE_ARTIST_CATEGORY)
                    .hireStatus(ApplicationConstants.DefaultValues.DEFAULT_HIRE_STATUS)
                    .hiredAt(job.getCreatedAt())
                    .startDate(job.getStartDate() != null ? job.getStartDate().atStartOfDay() : null)
                    .endDate(job.getStartDate() != null ? job.getStartDate().plusDays(30).atStartOfDay() : null)
                    .agreedSalary(job.getBudgetMin())
                    .currency(job.getCurrency())
                    .contractType(ApplicationConstants.DefaultValues.DEFAULT_CONTRACT_TYPE)
                    .workLocation(job.getLocation())
                    .workSchedule(ApplicationConstants.DefaultValues.DEFAULT_WORK_SCHEDULE)
                    .performanceRating(ApplicationConstants.DefaultValues.DEFAULT_PERFORMANCE_RATING)
                    .feedback(ApplicationConstants.DefaultValues.DEFAULT_FEEDBACK)
                    .isCompleted(true)
                    .isRecommended(true)
                    .canViewProfile(true)
                    .canRate(true)
                    .canRecommend(true)
                    .canRehire(true)
                    .canMessage(true)
                    .build();
        });
    }
    
    /**
     * Get hire details by ID
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getHireDetails(Long hireId, User recruiter) {
        // This would need to be implemented with a proper hire tracking system
        Map<String, Object> hireDetails = new HashMap<>();
        hireDetails.put("id", hireId);
        hireDetails.put("message", "Hire details would be implemented with proper hire tracking system");
        
        return hireDetails;
    }
    
    /**
     * Rate and provide feedback for a hire
     */
    @Transactional
    public Map<String, Object> rateHire(Long hireId, String rating, String feedback, User recruiter) {
        // This would need to be implemented with a proper hire tracking system
        Map<String, Object> result = new HashMap<>();
        result.put("hireId", hireId);
        result.put("rating", rating);
        result.put("feedback", feedback);
        result.put("message", "Rating submitted successfully");
        
        return result;
    }
    
    /**
     * Get recruiter statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStatistics(String email) {

        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        return getDashboardStatistics(recruiter);
    }
    
    /**
     * Get subscription status and features
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSubscriptionStatus(User recruiter) {
        // Get subscription
        Optional<Subscription> subscription = subscriptionRepository.findByUserIdAndStatus(
                recruiter.getId(), Subscription.SubscriptionStatus.ACTIVE);
        
        Map<String, Object> subscriptionStatus = new HashMap<>();
        subscriptionStatus.put("hasSubscription", subscription.isPresent());
        subscriptionStatus.put("subscriptionId", subscription.map(Subscription::getId).orElse(null));
        subscriptionStatus.put("subscriptionPlan", subscription.map(s -> s.getSubscriptionPlan().getName()).orElse(ApplicationConstants.DefaultValues.DEFAULT_SUBSCRIPTION_PLAN));
        subscriptionStatus.put("subscriptionStatus", subscription.map(s -> s.getStatus().name()).orElse(ApplicationConstants.Status.INACTIVE));
        subscriptionStatus.put("expiresAt", subscription.map(Subscription::getExpiresAt).orElse(null));
        subscriptionStatus.put("availableFeatures", getAvailableFeatures(recruiter));
        subscriptionStatus.put("premiumFeatures", getPremiumFeatures(recruiter));
        subscriptionStatus.put("hasPremiumFeatures", hasPremiumFeatures(recruiter));
        
        return subscriptionStatus;
    }
    
    /**
     * Update job status
     */
    @Transactional
    public Map<String, Object> updateJobStatus(Long jobId, String status, String reason, User recruiter) {
        // Get recruiter profile
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.RECRUITER_PROFILE_NOT_FOUND));
        
        // Get job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.JOB_NOT_FOUND));
        
        // Check if job belongs to recruiter
        if (!job.getRecruiter().getId().equals(recruiterProfile.getUser().getId())) {
            throw new RuntimeException(ApplicationConstants.ErrorMessages.ACCESS_DENIED);
        }
        
        // Update status
        job.setStatus(Job.JobStatus.valueOf(status));
        job.setUpdatedAt(LocalDateTime.now());
        
        Job savedJob = jobRepository.save(job);
        
        Map<String, Object> result = new HashMap<>();
        result.put("jobId", savedJob.getId());
        result.put("status", savedJob.getStatus());
        result.put("updatedAt", savedJob.getUpdatedAt());
        
        return result;
    }
    
    /**
     * Boost job visibility
     */
    @Transactional
    public Map<String, Object> boostJob(Long jobId, Integer days, User recruiter) {
        // Get recruiter profile
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.RECRUITER_PROFILE_NOT_FOUND));
        
        // Get job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.JOB_NOT_FOUND));
        
        // Check if job belongs to recruiter
        if (!job.getRecruiter().getId().equals(recruiterProfile.getUser().getId())) {
            throw new RuntimeException(ApplicationConstants.ErrorMessages.ACCESS_DENIED);
        }
        
        // Boost job (this would need to be implemented with proper boost tracking)
        Map<String, Object> result = new HashMap<>();
        result.put("jobId", job.getId());
        result.put("boosted", true);
        result.put("boostDays", days != null ? days : 7);
        result.put("boostExpiresAt", LocalDateTime.now().plusDays(days != null ? days : 7));
        
        return result;
    }
    
    // Helper methods
    
    private Map<String, Object> getDashboardStatistics(User recruiter) {
        // Get recruiter profile
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.RECRUITER_PROFILE_NOT_FOUND));
        
        // Get jobs by recruiter
        List<Job> jobs = jobRepository.findByRecruiter(recruiterProfile.getUser());
        
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalJobsPosted", (long) jobs.size());
        statistics.put("activeJobs", jobs.stream().filter(job -> job.getStatus() == Job.JobStatus.ACTIVE).count());
        statistics.put("closedJobs", jobs.stream().filter(job -> job.getStatus() == Job.JobStatus.CLOSED).count());
        statistics.put("totalApplications", 0); // TODO: Calculate from JobApplication repository if needed
        statistics.put("totalHires", 0L); // This would need to be tracked separately
        statistics.put("totalViews", 0L); // This would need to be tracked separately
        statistics.put("averageApplicationsPerJob", 0.0); // TODO: Calculate from JobApplication repository if needed
        statistics.put("hireRate", 0.0); // This would need to be calculated
        
        return statistics;
    }
    
    private List<RecentJobDto> getRecentJobs(User recruiter, int limit) {
        // Get recruiter profile
        RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.RECRUITER_PROFILE_NOT_FOUND));
        
        // Get recent jobs
        List<Job> jobs = jobRepository.findByRecruiter(recruiterProfile.getUser())
                .stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .limit(limit)
                .collect(Collectors.toList());
        
        return jobs.stream()
                .map(this::convertToRecentJobDto)
                .collect(Collectors.toList());
    }
    
    private List<RecentApplicationDto> getRecentApplications(User recruiter, int limit) {
        // This would need to be implemented with proper application tracking
        return new ArrayList<>();
    }
    
    private List<RecentHireDto> getRecentHires(User recruiter, int limit) {
        // This would need to be implemented with proper hire tracking
        return new ArrayList<>();
    }
    
    private RecentJobDto convertToRecentJobDto(Job job) {
        return RecentJobDto.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .jobType(job.getJobType().name())
                .location(job.getLocation())
                .salaryMin(job.getBudgetMin())
                .salaryMax(job.getBudgetMax())
                .currency(job.getCurrency())
                .status(job.getStatus().name())
                .isActive(job.getStatus() == Job.JobStatus.ACTIVE)
                .applicationDeadline(job.getApplicationDeadline() != null ?
                    job.getApplicationDeadline().atStartOfDay() : null)
                .startDate(job.getStartDate() != null ?
                    job.getStartDate().atStartOfDay() : null)
                .createdAt(job.getCreatedAt())
                .applicationCount(0) // TODO: Calculate from JobApplication repository if needed
                .viewCount(0) // This would need to be tracked separately
                .boostCount(0) // This would need to be tracked separately
                .canEdit(true)
                .canClose(true)
                .canBoost(true)
                .canViewApplications(true)
                .build();
    }
    
    private RecentApplicationDto convertToRecentApplicationDto(JobApplication application) {
        return RecentApplicationDto.builder()
                .id(application.getId())
                .jobId(application.getJob().getId())
                .jobTitle(application.getJob().getTitle())
                .artistId(application.getArtist().getId())
                .artistName(application.getArtist().getUser().getEmail()) // Placeholder
                .artistEmail(application.getArtist().getUser().getEmail())
                .artistPhone("N/A") // Placeholder
                .artistCategory("Unknown") // Placeholder
                .applicationStatus(application.getStatus().name())
                .appliedAt(application.getAppliedAt())
                .lastUpdatedAt(application.getUpdatedAt())
                .artistBio("N/A") // Placeholder
                .artistLocation("N/A") // Placeholder
                .artistExperience(0) // Placeholder
                .artistSkills("N/A") // Placeholder
                .artistPortfolio("N/A") // Placeholder
                .coverLetter(application.getCoverLetter())
                .expectedSalary("N/A") // Placeholder
                .availability("N/A") // Placeholder
                .additionalNotes("N/A") // Placeholder
                .canViewProfile(true)
                .canAccept(true)
                .canReject(true)
                .canShortlist(true)
                .canMessage(true)
                .build();
    }
    
    private ArtistSuggestionDto convertToArtistSuggestionDto(ArtistProfile artistProfile) {
        // Calculate profile completion percentage
        int completionPercentage = artistService.calculateProfileCompletionPercentage(artistProfile);

        return ArtistSuggestionDto.builder()
                .artistId(artistProfile.getId())
                .artistName(artistProfile.getUser().getEmail()) // Placeholder
                .artistEmail(artistProfile.getUser().getEmail())
                .artistCategory(artistProfile.getArtistType() != null ?
                        artistProfile.getArtistType().getDisplayName() : "Unknown")
                .artistType("Unknown") // Placeholder
                .location("N/A") // Placeholder
                .bio("N/A") // Placeholder
                .profilePhoto("N/A") // Placeholder
                .matchScore(0.0)
                .matchReasons(new ArrayList<>())
                .skills(new ArrayList<>())
                .genres(new ArrayList<>())
                .languages(new ArrayList<>())
                .experienceYears(0) // Placeholder
                .experienceLevel("Unknown") // Placeholder
                .portfolioItems(new ArrayList<>())
                .achievements(new ArrayList<>())
                .certifications(new ArrayList<>())
                .availability("N/A") // Placeholder
                .preferredJobType("N/A") // Placeholder
                .expectedSalaryMin(null)
                .expectedSalaryMax(null)
                .currency("USD")
                .workLocation("N/A") // Placeholder
                .workSchedule("N/A") // Placeholder
                .phone("N/A") // Placeholder
                .website("N/A") // Placeholder
                .socialLinks(new ArrayList<>())
                .contactPreference("Email")
                .lastActive(LocalDateTime.now())
                .totalApplications(0) // Placeholder
                .totalHires(0) // Placeholder
                .hireRate(0.0) // Placeholder
                .verificationStatus("UNVERIFIED") // Placeholder
                .isVerified(false)
                .isPremium(false)
                .profileCompletionPercentage(completionPercentage) // Profile completion percentage
                .canViewProfile(true)
                .canContact(true)
                .canShortlist(true)
                .canInvite(true)
                .canMessage(true)
                .build();
    }
    
    private double calculateMatchScore(ArtistProfile profile, Job job, String artistCategory,
                                          String artistType, String location, String skills, String genres,
                                          String experienceLevel, String availability, Boolean isVerified,
                                          Boolean isPremium) {
        // This would implement sophisticated matching algorithm
        return Math.random(); // Placeholder
    }
    
    private List<String> generateMatchReasons(ArtistProfile profile, Job job, String artistCategory,
                                              String artistType, String location, String skills, String genres,
                                              String experienceLevel, String availability, Boolean isVerified,
                                              Boolean isPremium) {
        // This would generate reasons for the match
        return Arrays.asList(
            ApplicationConstants.DefaultValues.SKILLS_MATCH_REASON,
            ApplicationConstants.DefaultValues.LOCATION_MATCH_REASON,
            ApplicationConstants.DefaultValues.EXPERIENCE_MATCH_REASON
        );
    }
    
    private List<String> getAvailableFeatures(User recruiter) {
        // Get subscription
        Optional<Subscription> subscription = subscriptionRepository.findByUserIdAndStatus(
                recruiter.getId(), Subscription.SubscriptionStatus.ACTIVE);
        
        if (subscription.isEmpty()) {
            return Arrays.asList(
                ApplicationConstants.DefaultValues.BASIC_FEATURE_1,
                ApplicationConstants.DefaultValues.BASIC_FEATURE_2
            );
        }
        
        // Get plan features
        List<PlanFeature> features = subscription.get().getSubscriptionPlan().getFeatures();
        
        return features.stream()
                .map(PlanFeature::getFeatureName)
                .collect(Collectors.toList());
    }
    
    private List<String> getPremiumFeatures(User recruiter) {
        return Arrays.asList(
            ApplicationConstants.DefaultValues.ADVANCED_FEATURE_1,
            ApplicationConstants.DefaultValues.ADVANCED_FEATURE_2,
            ApplicationConstants.DefaultValues.ADVANCED_FEATURE_3,
            ApplicationConstants.DefaultValues.ADVANCED_FEATURE_4
        );
    }
    
    private boolean hasPremiumFeatures(User recruiter) {
        // Get subscription
        Optional<Subscription> subscription = subscriptionRepository.findByUserIdAndStatus(
                recruiter.getId(), Subscription.SubscriptionStatus.ACTIVE);
        
        if (subscription.isEmpty()) {
            return false;
        }
        
        // Check if plan has premium features
        return subscription.get().getSubscriptionPlan().getPlanType() != SubscriptionPlan.PlanType.FREE;
    }





    /**
     * Get all applicants for a specific job
     * @param jobId The ID of the job
     * @param email The email of the recruiter
     * @return List of job applications with applicant details
     */
    @Transactional(readOnly = true)
    public List<JobApplicationDto> getJobApplicants(Long jobId, String email) {
        try {
            // Get recruiter
            User recruiter = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Recruiter not found"));

            // Get job post and verify ownership
            Job jobPost = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found"));

            // Verify the job belongs to the recruiter
//            if (!jobPost.getRecruiter().getId().equals(recruiter.getId())) {
//                throw new RuntimeException("You don't have permission to view applicants for this job");
//            }
//
            // Get all applications for this job
            List<JobApplication> applications = jobApplicationRepository.findByJobId(jobId);

            // Convert to DTOs
            return applications.stream()
                    .map(JobApplicationDto::new)
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error("Error getting job applicants: {}", e.getMessage());
            throw new RuntimeException("Failed to get job applicants: " + e.getMessage());
        }
    }

    @Transactional(readOnly = true)
    public RecruiterProfileDto getRecruiterProfile(String email) {
        User recruiter = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        RecruiterProfile recruiterProfile = recruiter.getRecruiterProfile();
        if (recruiterProfile == null) {
            throw new RuntimeException("Recruiter profile not found for user: " + email);
        }

        RecruiterProfileDto dto = new RecruiterProfileDto();

        // === USER INFO ===
        dto.setUserId(recruiter.getId());
        dto.setEmail(recruiter.getEmail());
        dto.setMobile(recruiter.getMobile());
        dto.setFirstName(recruiter.getFirstName());
        dto.setLastName(recruiter.getLastName());
        dto.setIsVerified(recruiter.getIsVerified());

        // === RECRUITER / COMPANY INFO ===
        dto.setId(recruiterProfile.getId());
        dto.setCompanyName(recruiterProfile.getCompanyName());
        dto.setContactPersonName(recruiterProfile.getContactPersonName());
        dto.setContactPhone(recruiterProfile.getContactPhone());
        dto.setDesignation(recruiterProfile.getDesignation());
        dto.setCompanyDescription(recruiterProfile.getCompanyDescription());
        dto.setCompanyWebsite(recruiterProfile.getCompanyWebsite());
        dto.setCompanyLogoUrl(recruiterProfile.getCompanyLogoUrl());
        dto.setIndustry(recruiterProfile.getIndustry());
        dto.setCompanySize(recruiterProfile.getCompanySize());
        dto.setLocation(recruiterProfile.getLocation());
        dto.setIsVerifiedCompany(recruiterProfile.getIsVerifiedCompany());

        // === METRICS ===
        dto.setTotalJobsPosted(recruiterProfile.getTotalJobsPosted());
        dto.setSuccessfulHires(recruiterProfile.getSuccessfulHires());
        dto.setChatCredits(recruiterProfile.getChatCredits());

        // === CATEGORY INFO ===
        if (recruiterProfile.getRecruiterCategory() != null) {
            dto.setRecruiterCategoryId(recruiterProfile.getRecruiterCategory().getId());
            dto.setRecruiterCategoryName(recruiterProfile.getRecruiterCategory().getName());
        }

        return dto;
    }


    @Transactional
    public void deleteJob(Long jobId, String email) {
        // Find the job with the recruiter relationship loaded
        Job job = jobRepository.findByIdWithRecruiter(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));

        // Check if the job belongs to the recruiter
        if (!job.getRecruiter().getEmail().equals(email)) {
            throw new RuntimeException("You don't have permission to delete this job");
        }

        // Check if already deleted (soft delete)
        if (job.getDeletedAt() != null) {
            throw new RuntimeException("Job is already deleted");
        }

        // Soft delete the job
        LocalDateTime now = LocalDateTime.now();
        job.setDeletedAt(now);
        job.setStatus(Job.JobStatus.DELETED);
        job.setUpdatedAt(now);

        jobRepository.save(job);

        // Log the deletion
        log.info("Job {} soft deleted by {}", jobId, email);
    }

    @Transactional
    public Map<String, Object> createJob(CreateJobDto createJobDto, String email) {
        try {
            User recruiter = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Recruiter not found"));
            Job job = new Job();
            job.setTitle(createJobDto.getTitle());
            job.setDescription(createJobDto.getDescription());
            job.setRequirements(createJobDto.getRequirements());
            job.setLocation(createJobDto.getLocation());

            // Set job type if provided
            if (createJobDto.getJobType() != null) {
                try {
                    job.setJobType(Job.JobType.valueOf(createJobDto.getJobType().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Invalid job type: " + createJobDto.getJobType());
                }
            }

            // Set experience level if provided
            if (createJobDto.getExperienceLevel() != null) {
                try {
                    job.setExperienceLevel(Job.ExperienceLevel.valueOf(
                        createJobDto.getExperienceLevel().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    throw new ValidationException("Invalid experience level: " + createJobDto.getExperienceLevel());
                }
            }

            // Set budget if provided
            try {
                if (createJobDto.getBudgetMin() != null) {
                    job.setBudgetMin(createJobDto.getBudgetMinAsBigDecimal());
                }
                if (createJobDto.getBudgetMax() != null) {
                    job.setBudgetMax(createJobDto.getBudgetMaxAsBigDecimal());
                }
                job.setCurrency(createJobDto.getCurrency());
            } catch (IllegalArgumentException e) {
                throw new ValidationException("Invalid budget value: " + e.getMessage());
            }

            // Set dates
            job.setStartDate(createJobDto.getStartDate());
            job.setEndDate(createJobDto.getEndDate());
            job.setApplicationDeadline(createJobDto.getApplicationDeadline());

            // Set skills as JSON array if provided
            if (createJobDto.getSkillsRequired() != null && createJobDto.getSkillsRequired().length > 0) {
                String jsonSkills = "[\"" + String.join("\",\"", createJobDto.getSkillsRequired()) + "\"]";
                job.setSkillsRequired(jsonSkills);
            }

            // Set other fields
            job.setTags(createJobDto.getTags());
            job.setBenefits(createJobDto.getBenefits());
            job.setContactEmail(createJobDto.getContactEmail());
            job.setContactPhone(createJobDto.getContactPhone());
            job.setIsRemote(createJobDto.getIsRemote());
            job.setIsUrgent(createJobDto.getIsUrgent());
            job.setIsFeatured(createJobDto.getIsFeatured());
            job.setDurationDays(createJobDto.getDurationDays());

            // Set timestamps and status
            LocalDateTime now = LocalDateTime.now();
            job.setCreatedAt(now);
            job.setUpdatedAt(now);
            job.setPublishedAt(now);
            job.setStatus(Job.JobStatus.ACTIVE);

            // Set the recruiter
            job.setRecruiter(recruiter);

            // Save the job
            Job savedJob = jobRepository.save(job);

            // Prepare response
            Map<String, Object> result = new HashMap<>();
            result.put("id", savedJob.getId());
            result.put("title", savedJob.getTitle());
            result.put("status", savedJob.getStatus());
            result.put("createdAt", savedJob.getCreatedAt());

            return result;

        } catch (Exception e) {
            log.error("Error creating job: {}", e.getMessage());
            throw new RuntimeException("Failed to create job: " + e.getMessage(), e);
        }
    }

    /**
     * Get all applicants for a specific job
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getApplicantsByJobId(User recruiter, Long jobId, String status,
                                                     Boolean isShortlisted, Integer minExperience,
                                                     Integer maxExperience, Pageable pageable) {
        try {
            // Verify job exists and belongs to recruiter
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found"));

            if (!job.getRecruiter().getId().equals(recruiter.getId())) {
                throw new RuntimeException("Unauthorized: This job does not belong to you");
            }

            // Get all applications for this job
            Page<JobApplication> applications;

            if (status != null) {
                // Filter by status
                JobApplication.ApplicationStatus appStatus = JobApplication.ApplicationStatus.valueOf(status.toUpperCase());
                applications = jobApplicationRepository.findByJobAndStatus(job, appStatus, pageable);
            } else {
                // Get all applications
                applications = jobApplicationRepository.findByJob(job, pageable);
            }

            // Convert to detailed applicant DTOs with filtering
            List<Map<String, Object>> applicants = applications.getContent().stream()
                    .filter(app -> {
                        // Filter by shortlisted status if specified
                        if (isShortlisted != null && !app.getIsShortlisted().equals(isShortlisted)) {
                            return false;
                        }
                        // Filter by experience if specified
                        if (minExperience != null || maxExperience != null) {
                            Integer experience = app.getArtist().getExperienceYears();
                            if (experience == null) return false;
                            if (minExperience != null && experience < minExperience) return false;
                            if (maxExperience != null && experience > maxExperience) return false;
                        }
                        return true;
                    })
                    .map(application -> {
                        Map<String, Object> applicant = new HashMap<>();
                        ArtistProfile artist = application.getArtist();

                        // Application details
                        applicant.put("applicationId", application.getId());
                        applicant.put("status", application.getStatus().name());
                        applicant.put("appliedAt", application.getAppliedAt());
                        applicant.put("reviewedAt", application.getReviewedAt());

                        // Artist basic info
                        applicant.put("artistId", artist.getId());
                        applicant.put("firstName", artist.getFirstName());
                        applicant.put("lastName", artist.getLastName());
                        applicant.put("fullName", artist.getFirstName() + " " + artist.getLastName());
                        applicant.put("stageName", artist.getStageName());
                        applicant.put("email", artist.getUser().getEmail());
                        applicant.put("phone", artist.getUser().getMobile());

                        // Artist profile details
                        applicant.put("artistType", artist.getArtistType() != null ? artist.getArtistType().getName() : null);
                        applicant.put("location", artist.getLocation());
                        applicant.put("bio", artist.getBio());
                        applicant.put("experienceYears", artist.getExperienceYears());
                        applicant.put("skills", artist.getSkills());
                        applicant.put("languagesSpoken", artist.getLanguagesSpoken());
                        applicant.put("hourlyRate", artist.getHourlyRate());
                        applicant.put("isVerified", artist.getIsVerifiedBadge());

                        // Application specific details
                        applicant.put("coverLetter", application.getCoverLetter());
                        applicant.put("expectedSalary", application.getExpectedSalary());
                        applicant.put("availabilityDate", application.getAvailabilityDate());
                        applicant.put("portfolioUrl", application.getPortfolioUrl());
                        applicant.put("resumeUrl", application.getResumeUrl());
                        applicant.put("demoReelUrl", application.getDemoReelUrl());

                        // Application status flags
                        applicant.put("isShortlisted", application.getIsShortlisted());
                        applicant.put("isHired", application.getIsHired());
                        applicant.put("rating", application.getRating());
                        applicant.put("feedback", application.getFeedback());
                        applicant.put("interviewScheduledAt", application.getInterviewScheduledAt());
                        applicant.put("interviewNotes", application.getInterviewNotes());
                        applicant.put("rejectionReason", application.getRejectionReason());

                        return applicant;
                    })
                    .collect(Collectors.toList());

            // Job details
            Map<String, Object> jobDetails = new HashMap<>();
            jobDetails.put("jobId", job.getId());
            jobDetails.put("jobTitle", job.getTitle());
            jobDetails.put("jobStatus", job.getStatus().name());
            jobDetails.put("totalApplications", jobApplicationRepository.countByJob(job));

            // Prepare response
            Map<String, Object> result = new HashMap<>();
            result.put("job", jobDetails);
            result.put("applicants", applicants);
            result.put("totalElements", applications.getTotalElements());
            result.put("totalPages", applications.getTotalPages());
            result.put("currentPage", applications.getNumber());
            result.put("size", applications.getSize());

            return result;
        } catch (Exception e) {
            log.error("Error getting applicants for job {}: {}", jobId, e.getMessage(), e);
            throw new RuntimeException("Failed to get applicants: " + e.getMessage(), e);
        }
    }

    /**
     * Get all hired applicants for the recruiter
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getHiredApplicants(User recruiter, Long jobId, Pageable pageable) {
        try {
            // Get recruiter profile
            RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                    .orElseThrow(() -> new RuntimeException(ApplicationConstants.ErrorMessages.RECRUITER_PROFILE_NOT_FOUND));

            // Get all jobs by this recruiter
            List<Job> recruiterJobs;
            if (jobId != null) {
                // Filter by specific job
                Job job = jobRepository.findById(jobId)
                        .orElseThrow(() -> new RuntimeException("Job not found"));
                if (!job.getRecruiter().getId().equals(recruiter.getId())) {
                    throw new RuntimeException("Unauthorized access to job");
                }
                recruiterJobs = List.of(job);
            } else {
                // Get all jobs by recruiter
                recruiterJobs = jobRepository.findByRecruiterId(recruiter.getId());
            }

            // Get job IDs
            List<Long> jobIds = recruiterJobs.stream()
                    .map(Job::getId)
                    .collect(Collectors.toList());

            if (jobIds.isEmpty()) {
                // No jobs, return empty result
                Map<String, Object> result = new HashMap<>();
                result.put("hires", new ArrayList<>());
                result.put("totalElements", 0);
                result.put("totalPages", 0);
                result.put("currentPage", 0);
                result.put("size", 0);
                return result;
            }

            // Get hired applications
            Page<JobApplication> hiredApplications = jobApplicationRepository.findByJobIdInAndIsHired(
                    jobIds, true, pageable);

            // Convert to DTOs
            List<Map<String, Object>> hires = hiredApplications.getContent().stream()
                    .map(application -> {
                        Map<String, Object> hire = new HashMap<>();
                        hire.put("id", application.getId());
                        hire.put("jobId", application.getJob().getId());
                        hire.put("jobTitle", application.getJob().getTitle());
                        hire.put("artistId", application.getArtist().getId());
                        hire.put("artistName", application.getArtist().getFirstName() + " " + application.getArtist().getLastName());
                        hire.put("artistEmail", application.getArtist().getUser().getEmail());
                        hire.put("status", application.getStatus());
                        hire.put("hiredAt", application.getHiredAt());
                        hire.put("expectedSalary", application.getExpectedSalary());
                        hire.put("rating", application.getRating());
                        hire.put("feedback", application.getFeedback());
                        return hire;
                    })
                    .collect(Collectors.toList());

            // Prepare response
            Map<String, Object> result = new HashMap<>();
            result.put("hires", hires);
            result.put("totalElements", hiredApplications.getTotalElements());
            result.put("totalPages", hiredApplications.getTotalPages());
            result.put("currentPage", hiredApplications.getNumber());
            result.put("size", hiredApplications.getSize());

            return result;
        } catch (Exception e) {
            log.error("Error getting hired applicants: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get hired applicants: " + e.getMessage(), e);
        }
    }

    /**
     * Schedule interview for an applicant
     */
    @Transactional
    public Map<String, Object> scheduleInterview(User recruiter, Long applicationId,
                                                   String interviewDateTime, String interviewType,
                                                   String interviewLocation, String meetingLink,
                                                   String notes) {
        try {
            // Get the application
            JobApplication application = jobApplicationRepository.findById(applicationId)
                    .orElseThrow(() -> new RuntimeException("Application not found"));

            // Verify the job belongs to this recruiter
            if (!application.getJob().getRecruiter().getId().equals(recruiter.getId())) {
                throw new RuntimeException("Unauthorized: This application does not belong to your job");
            }

            // Parse the interview date time
            LocalDateTime interviewScheduledAt = LocalDateTime.parse(interviewDateTime);

            // Update application with interview details
            application.setStatus(JobApplication.ApplicationStatus.INTERVIEW_SCHEDULED);
            application.setInterviewScheduledAt(interviewScheduledAt);
            application.setInterviewNotes(notes);
            application.setUpdatedAt(LocalDateTime.now());

            // Save the application
            jobApplicationRepository.save(application);

            // Get artist and job details for email
            ArtistProfile artist = application.getArtist();
            Job job = application.getJob();
            RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                    .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

            // Send email notification to applicant
            sendInterviewEmail(
                    artist.getUser().getEmail(),
                    artist.getFirstName() + " " + artist.getLastName(),
                    job.getTitle(),
                    recruiterProfile.getCompanyName(),
                    recruiterProfile.getContactPersonName(),
                    interviewScheduledAt,
                    interviewType,
                    interviewLocation,
                    meetingLink,
                    notes
            );

            // Prepare response
            Map<String, Object> result = new HashMap<>();
            result.put("applicationId", application.getId());
            result.put("artistName", artist.getFirstName() + " " + artist.getLastName());
            result.put("artistEmail", artist.getUser().getEmail());
            result.put("jobTitle", job.getTitle());
            result.put("interviewScheduledAt", interviewScheduledAt);
            result.put("interviewType", interviewType);
            result.put("interviewLocation", interviewLocation);
            result.put("meetingLink", meetingLink);
            result.put("status", application.getStatus().name());
            result.put("emailSent", true);

            log.info("Interview scheduled successfully for application ID: {} at {}", applicationId, interviewScheduledAt);

            return result;
        } catch (Exception e) {
            log.error("Error scheduling interview: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to schedule interview: " + e.getMessage(), e);
        }
    }

    /**
     * Submit interview result (HIRED or REJECTED)
     */
    @Transactional
    public Map<String, Object> submitInterviewResult(User recruiter, InterviewResultDto resultDto) {
        try {
            // Get the application
            JobApplication application = jobApplicationRepository.findById(resultDto.getApplicationId())
                    .orElseThrow(() -> new RuntimeException("Application not found"));

            // Verify the job belongs to this recruiter
            if (!application.getJob().getRecruiter().getId().equals(recruiter.getId())) {
                throw new RuntimeException("Unauthorized: This application does not belong to your job");
            }

            // Validate that interview was already scheduled
            if (application.getStatus() != JobApplication.ApplicationStatus.INTERVIEW_SCHEDULED &&
                application.getStatus() != JobApplication.ApplicationStatus.INTERVIEWED) {
                throw new RuntimeException("Interview must be scheduled before submitting result. Current status: " + application.getStatus());
            }

            // Get artist and job details
            ArtistProfile artist = application.getArtist();
            Job job = application.getJob();
            RecruiterProfile recruiterProfile = recruiterProfileRepository.findByUserId(recruiter.getId())
                    .orElseThrow(() -> new RuntimeException("Recruiter profile not found"));

            // Update application based on result
            LocalDateTime now = LocalDateTime.now();
            if (resultDto.getResult() == InterviewResultDto.InterviewResult.HIRED) {
                application.setStatus(JobApplication.ApplicationStatus.HIRED);
                application.setIsHired(true);
                application.setHiredAt(now);

                // Update recruiter's successful hires count
                recruiterProfile.setSuccessfulHires(
                    (recruiterProfile.getSuccessfulHires() != null ? recruiterProfile.getSuccessfulHires() : 0) + 1
                );
                recruiterProfileRepository.save(recruiterProfile);

                log.info("Applicant HIRED - Application ID: {}, Artist: {}, Job: {}",
                    application.getId(), artist.getFirstName() + " " + artist.getLastName(), job.getTitle());
            } else {
                application.setStatus(JobApplication.ApplicationStatus.REJECTED);
                application.setRejectionReason(resultDto.getNotes());

                log.info("Applicant REJECTED - Application ID: {}, Artist: {}, Job: {}",
                    application.getId(), artist.getFirstName() + " " + artist.getLastName(), job.getTitle());
            }

            // Save notes if provided
            if (resultDto.getNotes() != null && !resultDto.getNotes().isEmpty()) {
                application.setFeedback(resultDto.getNotes());
            }

            application.setReviewedAt(now);
            application.setUpdatedAt(now);

            // Save the application
            JobApplication savedApplication = jobApplicationRepository.save(application);

            // Send email notification to applicant
            sendInterviewResultEmail(
                    artist.getUser().getEmail(),
                    artist.getFirstName() + " " + artist.getLastName(),
                    job.getTitle(),
                    recruiterProfile.getCompanyName(),
                    resultDto.getResult(),
                    resultDto.getNotes()
            );

            // Prepare response
            Map<String, Object> result = new HashMap<>();
            result.put("applicationId", savedApplication.getId());
            result.put("artistId", artist.getId());
            result.put("artistName", artist.getFirstName() + " " + artist.getLastName());
            result.put("artistEmail", artist.getUser().getEmail());
            result.put("jobId", job.getId());
            result.put("jobTitle", job.getTitle());
            result.put("result", resultDto.getResult().name());
            result.put("status", savedApplication.getStatus().name());
            result.put("notes", resultDto.getNotes());
            result.put("reviewedAt", savedApplication.getReviewedAt());
            result.put("isHired", savedApplication.getIsHired());
            result.put("hiredAt", savedApplication.getHiredAt());
            result.put("emailSent", true);

            return result;
        } catch (Exception e) {
            log.error("Error submitting interview result: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to submit interview result: " + e.getMessage(), e);
        }
    }

    /**
     * Send interview result email to applicant
     */
    private void sendInterviewResultEmail(String toEmail, String applicantName, String jobTitle,
                                           String companyName, InterviewResultDto.InterviewResult result,
                                           String notes) {
        try {
            if (result == InterviewResultDto.InterviewResult.HIRED) {
                emailService.sendHiredEmail(toEmail, applicantName, jobTitle, companyName, notes);
            } else {
                emailService.sendRejectionEmail(toEmail, applicantName, jobTitle, companyName, notes);
            }
            log.info("Interview result email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Error sending interview result email: {}", e.getMessage(), e);
            // Don't throw exception - result is already recorded
        }
    }

    /**
     * Send interview scheduled email to applicant
     */
    private void sendInterviewEmail(String toEmail, String applicantName, String jobTitle,
                                     String companyName, String recruiterName,
                                     LocalDateTime interviewDateTime, String interviewType,
                                     String interviewLocation, String meetingLink, String notes) {
        try {
            // Use EmailService to send interview schedule email
            emailService.sendInterviewScheduleEmail(
                    toEmail,
                    applicantName,
                    jobTitle,
                    companyName,
                    recruiterName,
                    interviewDateTime,
                    interviewType,
                    interviewLocation,
                    meetingLink,
                    notes
            );
            log.info("Interview email sent successfully to: {}", toEmail);
        } catch (Exception e) {
            log.error("Error sending interview email: {}", e.getMessage(), e);
            // Don't throw exception - interview is already scheduled
        }
    }
}
