package com.icastar.platform.dto.application;

import com.icastar.platform.entity.JobApplication;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateJobApplicationDto {
    
    @NotNull(message = "Job ID is required")
    private Long jobId;

    @NotBlank(message = "Cover letter cannot be empty")
    private String coverLetter;

    @NotNull(message = "Proposed rate is required")
    private BigDecimal proposedRate;
}
