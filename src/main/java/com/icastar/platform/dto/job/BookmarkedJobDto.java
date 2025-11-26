package com.icastar.platform.dto.job;

import lombok.Data;

import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookmarkedJobDto {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private String jobDescription;
    private String jobLocation;
    private String jobType;
    private String experienceLevel;
    private BigDecimal budgetMin;
    private BigDecimal budgetMax;
    private String currency;
    private Boolean isRemote;
    private Boolean isUrgent;
    private Boolean isFeatured;
    private String status;
    private Integer applicationsCount;
    private LocalDateTime bookmarkedAt;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

