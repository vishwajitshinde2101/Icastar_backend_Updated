package com.icastar.platform.dto.castingcall;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icastar.platform.entity.CastingCallApplication;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateApplicationStatusDto {

    @NotNull(message = "Status is required")
    private CastingCallApplication.ApplicationStatus status;

    private String notes;
    private Integer rating; // 1-5
    private String feedback;
    private String rejectionReason;

    // Callback scheduling
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime callbackDate;
    private String callbackLocation;
    private String callbackNotes;
}
