package com.icastar.platform.dto.recruiter;

import com.icastar.platform.entity.HireRequest;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHireRequestStatusDto {

    @NotNull(message = "Status is required")
    private HireRequest.HireRequestStatus status;

    private String notes;

    private String artistResponse;
}