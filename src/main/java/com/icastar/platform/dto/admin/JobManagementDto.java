package com.icastar.platform.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobManagementDto {
    
    private Long id;
    private String title;
    private String description;
    private String requirements;
    private String jobType;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String currency;
    private String status;
    private Boolean isActive;
    private Boolean isVisible;
    private LocalDateTime applicationDeadline;
    private LocalDateTime startDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Recruiter Information
    private Long recruiterId;
    private String recruiterName;
    private String recruiterEmail;
    private String recruiterCompany;
    private String recruiterCategory;
    
    // Subscription Information
    private Long subscriptionId;
    private String subscriptionPlan;
    private String subscriptionStatus;
    
    // Statistics
    private Integer applicationCount;
    private Integer viewCount;
    private Integer boostCount;
    private LocalDateTime lastBoostedAt;
    
    // Admin Management
    private String adminNotes;
    private LocalDateTime lastAdminAction;
    private String lastAdminActionBy;
}
