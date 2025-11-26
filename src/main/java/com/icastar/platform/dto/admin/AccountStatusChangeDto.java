package com.icastar.platform.dto.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatusChangeDto {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "New status is required")
    private String newStatus;
    
    @NotBlank(message = "Reason is required")
    private String reason;
    
    private String adminNotes;
    
    private String ipAddress;
    
    private String userAgent;
}
