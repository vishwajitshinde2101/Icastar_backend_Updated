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

    // For HIRED result
    private OfferDetails offerDetails;

    // For REJECTED result
    private String rejectionReason;

    public enum InterviewResult {
        HIRED,
        REJECTED
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OfferDetails {
        private Double salary;
        private String contractUrl;
    }
}
