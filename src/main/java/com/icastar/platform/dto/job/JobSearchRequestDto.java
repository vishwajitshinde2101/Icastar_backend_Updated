package com.icastar.platform.dto.job;

import com.icastar.platform.entity.Job;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class JobSearchRequestDto {
    
    // Search parameters
    private String searchTerm;
    private String jobTitle;
    private String companyName;
    
    // Filter parameters
    private Job.JobType jobType;
    private Job.ExperienceLevel experienceLevel;
    private Job.JobStatus status;
    
    // Pay range filters
    private BigDecimal minPay;
    private BigDecimal maxPay;
    private String currency;
    
    // Location filters
    private String location;
    private Boolean isRemote;
    
    // Additional filters
    private Boolean isUrgent;
    private Boolean isFeatured;
    private List<String> skills;
    private List<String> tags;
    
    // Pagination
    private Integer page = 0;
    private Integer size = 10;
    private String sortBy = "publishedAt";
    private String sortDirection = "desc";
}
