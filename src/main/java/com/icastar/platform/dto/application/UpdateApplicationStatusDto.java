package com.icastar.platform.dto.application;

import com.icastar.platform.entity.JobApplication;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateApplicationStatusDto {
    
    @NotNull(message = "Application status is required")
    private JobApplication.ApplicationStatus status;
    
    private String feedback;
    private String rejectionReason;
}
