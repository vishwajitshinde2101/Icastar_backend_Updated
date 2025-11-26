package com.icastar.platform.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountManagementResponseDto {
    
    private Long userId;
    private String email;
    private String mobile;
    private String firstName;
    private String lastName;
    private String currentStatus;
    private String previousStatus;
    private LocalDateTime statusChangedAt;
    private String changedBy;
    private String reason;
    private String adminNotes;
    private LocalDateTime lastActivity;
    private LocalDateTime deactivatedAt;
    private LocalDateTime reactivatedAt;
    private Boolean isActive;
    private String role;
}
