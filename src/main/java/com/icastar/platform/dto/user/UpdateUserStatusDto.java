package com.icastar.platform.dto.user;

import com.icastar.platform.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UpdateUserStatusDto {
    
    @NotNull(message = "User status is required")
    private User.UserStatus status;
    
    private String reason;
    
    private Boolean isVerified;
    
    private LocalDateTime accountLockedUntil;
}
