package com.icastar.platform.dto.recruiter;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResultDto {

    @NotNull(message = "Application ID is required")
    private Long applicationId;

    @NotNull(message = "Result is required (HIRED or REJECTED)")
    private InterviewResult result;

    private String notes;

    public enum InterviewResult {
        HIRED,
        REJECTED
    }
}
