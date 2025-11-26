package com.icastar.platform.dto.recruiter;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO for creating a new job in the jobs table
 */
@Data
public class CreateJobDto {
    private String title;
    private String description;
    private String requirements;
    private String location;
    private String jobType; // FULL_TIME, PART_TIME, CONTRACT, FREELANCE, INTERNSHIP, PROJECT_BASED
    private String experienceLevel; // ENTRY_LEVEL, MID_LEVEL, SENIOR_LEVEL, EXPERT_LEVEL
    private String budgetMin;
    private String budgetMax;
    private String currency = "INR";
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate applicationDeadline;
    private String[] skillsRequired;
    private String tags; // JSON array of tags
    private String benefits;
    private String contactEmail;
    private String contactPhone;
    private Boolean isRemote = false;
    private Boolean isUrgent = false;
    private Boolean isFeatured = false;
    private Integer durationDays;
    
    // Helper methods for budget conversion
    public BigDecimal getBudgetMinAsBigDecimal() {
        try {
            return budgetMin != null ? new BigDecimal(budgetMin) : null;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid budgetMin value: " + budgetMin);
        }
    }
    
    public BigDecimal getBudgetMaxAsBigDecimal() {
        try {
            return budgetMax != null ? new BigDecimal(budgetMax) : null;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid budgetMax value: " + budgetMax);
        }
    }
}
