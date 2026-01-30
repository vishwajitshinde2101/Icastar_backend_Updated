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
public class CreateHireRequestDto {

    @NotNull(message = "Artist ID is required")
    private Long artistId;

    private Long jobId;

    private String message;

    private Double offeredSalary;

    private String projectDetails;

    private String notes;
}