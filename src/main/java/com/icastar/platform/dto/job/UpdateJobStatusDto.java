package com.icastar.platform.dto.job;

import com.icastar.platform.entity.Job;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateJobStatusDto {

    @NotNull(message = "Job status is required")
    private Job.JobStatus status;

    private String reason; // Optional reason for status change (for audit purposes)
}
