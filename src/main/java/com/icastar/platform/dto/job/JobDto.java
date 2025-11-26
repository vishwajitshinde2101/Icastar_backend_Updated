package com.icastar.platform.dto.job;

import com.icastar.platform.entity.Job;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class JobDto {

    private Long id;
    private Long recruiterId;
    private String recruiterName;
    private String recruiterEmail;
    private String title;
    private String description;
    private String requirements;
    private String location;
    private Job.JobType jobType;
    private Job.ExperienceLevel experienceLevel;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String currency;
    private Integer durationDays;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate applicationDeadline;
    private Boolean isRemote;
    private Boolean isUrgent;
    private Boolean isFeatured;
    private Job.JobStatus status;
    private Integer viewsCount;
    private Integer applicationsCount;
    private List<String> tags;
    private List<String> skillsRequired;
    private String benefits;
    private String contactEmail;
    private String contactPhone;
    private LocalDateTime publishedAt;
    private LocalDateTime closedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Additional fields for candidate view
    private Boolean isBookmarked;
    private Boolean hasApplied;
    private Long applicationId;
    private JobApplicationDto applicationStatus;

    // Default constructor
    public JobDto() {}

    // Constructor that takes a Job entity
    public JobDto(Job job) {
        this.id = job.getId();
        this.recruiterId = job.getRecruiter().getId();
        this.recruiterName = job.getRecruiter().getFirstName() + " " + job.getRecruiter().getLastName();
        this.recruiterEmail = job.getRecruiter().getEmail();
        this.title = job.getTitle();
        this.description = job.getDescription();
        this.requirements = job.getRequirements();
        this.location = job.getLocation();
        this.jobType = job.getJobType();
        this.experienceLevel = job.getExperienceLevel();
        this.budgetMin = job.getBudgetMin();
        this.budgetMax = job.getBudgetMax();
        this.currency = job.getCurrency();
        this.durationDays = job.getDurationDays();
        this.startDate = job.getStartDate();
        this.endDate = job.getEndDate();
        this.applicationDeadline = job.getApplicationDeadline();
        this.isRemote = job.getIsRemote();
        this.isUrgent = job.getIsUrgent();
        this.isFeatured = job.getIsFeatured();
        this.status = job.getStatus();
        this.viewsCount = job.getViewsCount();
        this.applicationsCount = job.getApplicationsCount();
        // Parse JSON tags string to List<String> if not null
        if (job.getTags() != null && !job.getTags().isEmpty()) {
            try {
                // Simple JSON array parsing - assuming format like ["tag1","tag2"]
                String tagsStr = job.getTags().trim();
                if (tagsStr.startsWith("[") && tagsStr.endsWith("]")) {
                    tagsStr = tagsStr.substring(1, tagsStr.length() - 1);
                    this.tags = java.util.Arrays.asList(tagsStr.split(","));
                } else {
                    this.tags = java.util.Arrays.asList(tagsStr.split(","));
                }
            } catch (Exception e) {
                this.tags = java.util.Arrays.asList(job.getTags());
            }
        } else {
            this.tags = new java.util.ArrayList<>();
        }
        // Parse JSON skillsRequired string to List<String> if not null
        if (job.getSkillsRequired() != null && !job.getSkillsRequired().isEmpty()) {
            try {
                // Simple JSON array parsing - assuming format like ["skill1","skill2"]
                String skillsStr = job.getSkillsRequired().trim();
                if (skillsStr.startsWith("[") && skillsStr.endsWith("]")) {
                    skillsStr = skillsStr.substring(1, skillsStr.length() - 1);
                    this.skillsRequired = java.util.Arrays.asList(skillsStr.split(","));
                } else {
                    this.skillsRequired = java.util.Arrays.asList(skillsStr.split(","));
                }
            } catch (Exception e) {
                this.skillsRequired = java.util.Arrays.asList(job.getSkillsRequired());
            }
        } else {
            this.skillsRequired = new java.util.ArrayList<>();
        }
        this.benefits = job.getBenefits();
        this.contactEmail = job.getContactEmail();
        this.contactPhone = job.getContactPhone();
        this.publishedAt = job.getPublishedAt();
        this.closedAt = job.getClosedAt();
        this.createdAt = job.getCreatedAt();
        this.updatedAt = job.getUpdatedAt();
    }
}
