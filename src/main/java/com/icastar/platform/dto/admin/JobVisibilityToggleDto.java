package com.icastar.platform.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobVisibilityToggleDto {
    
    @NotNull(message = "Job ID is required")
    private Long jobId;
    
    @NotNull(message = "Visibility status is required")
    private Boolean isVisible;
    
    private String reason;
    private String adminNotes;
}
