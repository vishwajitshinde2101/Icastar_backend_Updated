package com.icastar.platform.dto.recruiter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class JobPostingDto {
    
    @NotBlank(message = "Job title is required")
    private String title;
    
    @NotBlank(message = "Job description is required")
    private String description;
    
    private String requirements;
    
    @NotNull(message = "Job type is required")
    private String jobType;
    
    private String location;
    
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String currency;
    
    private LocalDateTime applicationDeadline;
    private LocalDateTime startDate;
    
    // Job Categories
    private List<String> artistCategories;
    private List<String> skills;
    private List<String> genres;
    private List<String> languages;
    
    // Additional Information
    private String workLocation;
    private String workSchedule;
    private String contractType;
    private String experienceLevel;
    private String educationLevel;
    
    // Project Details
    private String projectName;
    private String projectType;
    private String projectDuration;
    private String projectBudget;
    private String projectDescription;
    
    // Contact Information
    private String contactPerson;
    private String contactEmail;
    private String contactPhone;
    private String companyWebsite;
    
    // Visibility and Boost
    private Boolean isVisible;
    private Boolean isBoosted;
    private LocalDateTime boostExpiresAt;
    private Integer boostCount;
    
    // Dynamic Fields (for custom job fields)
    private List<JobFieldDto> dynamicFields;
}
