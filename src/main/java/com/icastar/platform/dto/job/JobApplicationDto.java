package com.icastar.platform.dto.job;

import com.icastar.platform.entity.JobApplication;
import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class JobApplicationDto {

    private Long id;
    private Long jobId;
    private String jobTitle;
    private Long artistId;
    private String artistName;
    private String artistEmail;
    private JobApplication.ApplicationStatus status;
    private String coverLetter;
    private Double expectedSalary;
    private LocalDate availabilityDate;
    private String portfolioUrl;
    private String resumeUrl;
    private String demoReelUrl;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime interviewScheduledAt;
    private String interviewNotes;
    private String rejectionReason;
    private String feedback;
    private Integer rating;
    private Boolean isShortlisted;
    private Boolean isHired;
    private LocalDateTime hiredAt;
    private String contractUrl;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

