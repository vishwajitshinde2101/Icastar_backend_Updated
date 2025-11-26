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
public class RecentHireDto {
    
    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long artistId;
    private String artistName;
    private String artistEmail;
    private String artistCategory;
    private String hireStatus;
    private LocalDateTime hiredAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    // Contract Details
    private BigDecimal agreedSalary;
    private String currency;
    private String contractType;
    private String workLocation;
    private String workSchedule;
    
    // Performance
    private String performanceRating;
    private String feedback;
    private Boolean isCompleted;
    private Boolean isRecommended;
    
    // Quick Actions
    private Boolean canViewProfile;
    private Boolean canRate;
    private Boolean canRecommend;
    private Boolean canRehire;
    private Boolean canMessage;
}
