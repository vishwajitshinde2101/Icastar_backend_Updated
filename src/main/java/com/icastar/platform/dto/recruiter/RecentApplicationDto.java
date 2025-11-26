package com.icastar.platform.dto.recruiter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentApplicationDto {
    
    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long artistId;
    private String artistName;
    private String artistEmail;
    private String artistPhone;
    private String artistCategory;
    private String applicationStatus;
    private LocalDateTime appliedAt;
    private LocalDateTime lastUpdatedAt;
    
    // Artist Profile Summary
    private String artistBio;
    private String artistLocation;
    private Integer artistExperience;
    private String artistSkills;
    private String artistPortfolio;
    
    // Application Details
    private String coverLetter;
    private String expectedSalary;
    private String availability;
    private String additionalNotes;
    
    // Quick Actions
    private Boolean canViewProfile;
    private Boolean canAccept;
    private Boolean canReject;
    private Boolean canShortlist;
    private Boolean canMessage;
}
