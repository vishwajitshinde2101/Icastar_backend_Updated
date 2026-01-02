package com.icastar.platform.dto.castingcall;

import com.icastar.platform.entity.CastingCallApplication;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUpdateApplicationDto {

    @NotNull(message = "Application IDs are required")
    @NotEmpty(message = "Application IDs cannot be empty")
    private List<Long> applicationIds;

    @NotNull(message = "Status is required")
    private CastingCallApplication.ApplicationStatus status;

    private String notes;
    private String rejectionReason;
}
