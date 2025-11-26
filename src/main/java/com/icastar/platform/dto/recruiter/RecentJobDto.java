package com.icastar.platform.dto.recruiter;

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
public class RecentJobDto {
    
    private Long id;
    private String title;
    private String description;
    private String jobType;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String currency;
    private String status;
    private Boolean isActive;
    private LocalDateTime applicationDeadline;
    private LocalDateTime startDate;
    private LocalDateTime createdAt;
    
    // Statistics
    private Integer applicationCount;
    private Integer viewCount;
    private Integer boostCount;
    private LocalDateTime lastBoostedAt;
    
    // Quick Actions
    private Boolean canEdit;
    private Boolean canClose;
    private Boolean canBoost;
    private Boolean canViewApplications;
}
