package com.icastar.platform.dto.job;

import com.icastar.platform.entity.JobApplication;
import lombok.Data;

@Data
public class UpdateJobApplicationDto {

    private JobApplication.ApplicationStatus status;
    private String interviewNotes;
    private String rejectionReason;
    private String feedback;
    private Integer rating;
    private Boolean isShortlisted;
    private Boolean isHired;
    private String contractUrl;
    private String notes;
}
