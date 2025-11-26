package com.icastar.platform.dto.application;

import com.icastar.platform.entity.JobApplication;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class JobApplicationDto {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long artistId;
    private String artistName;
    private String coverLetter;
    private BigDecimal proposedRate;
    private JobApplication.ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
    
    // Artist profile information
    private String artistBio;
    private String artistLocation;
    private String artistExperience;
    private Boolean isArtistVerified;
    
    // Job information
    private String jobDescription;
    private String jobLocation;
    private Boolean isJobRemote;
    private BigDecimal jobBudgetMin;
    private BigDecimal jobBudgetMax;
    
    // Recruiter information
    private Long recruiterId;
    private String recruiterName;
    private String companyName;

    public JobApplicationDto(JobApplication application) {
        this.id = application.getId();
        this.jobId = application.getJob().getId();
        this.jobTitle = application.getJob().getTitle();
        this.artistId = application.getArtist().getId();
        this.coverLetter = application.getCoverLetter();
        this.proposedRate = application.getExpectedSalary() != null ? 
            BigDecimal.valueOf(application.getExpectedSalary()) : null;
        this.status = application.getStatus();
        this.appliedAt = application.getAppliedAt();
        this.updatedAt = application.getUpdatedAt();
        
        if (application.getArtist() != null) {
            this.artistName = application.getArtist().getFirstName() + " " + 
                            application.getArtist().getLastName();
            this.artistBio = application.getArtist().getBio();
            this.artistLocation = application.getArtist().getLocation();
            this.artistExperience = application.getArtist().getExperienceYears() != null ? 
                application.getArtist().getExperienceYears().toString() + " years" : "Not specified";
            this.isArtistVerified = application.getArtist().getUser() != null ? 
                application.getArtist().getUser().getIsVerified() : false;
        }
        
        if (application.getJob() != null) {
            this.jobDescription = application.getJob().getDescription();
            this.jobLocation = application.getJob().getLocation();
            this.isJobRemote = application.getJob().getIsRemote();
            this.jobBudgetMin = application.getJob().getBudgetMin();
            this.jobBudgetMax = application.getJob().getBudgetMax();
            
            if (application.getJob().getRecruiter() != null) {
                this.recruiterId = application.getJob().getRecruiter().getId();
                this.recruiterName = application.getJob().getRecruiter().getFirstName() + " " + 
                                   application.getJob().getRecruiter().getLastName();
                this.companyName = application.getJob().getRecruiter().getEmail(); // Using email as company name fallback
            }
        }
    }
}
