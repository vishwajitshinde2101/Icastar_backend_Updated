package com.icastar.platform.dto.job;

import lombok.Data;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class CreateJobApplicationDto {

    @Positive(message = "Job ID must be positive")
    private Long jobId;

    @Size(max = 2000, message = "Cover letter must not exceed 2000 characters")
    private String coverLetter;

    @Positive(message = "Expected salary must be positive")
    private Double expectedSalary;

    private LocalDate availabilityDate;

    @Size(max = 500, message = "Portfolio URL must not exceed 500 characters")
    private String portfolioUrl;

    @Size(max = 500, message = "Resume URL must not exceed 500 characters")
    private String resumeUrl;

    @Size(max = 500, message = "Demo reel URL must not exceed 500 characters")
    private String demoReelUrl;
}
