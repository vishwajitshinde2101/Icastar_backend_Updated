package com.icastar.platform.dto.auth;

import com.icastar.platform.entity.User;
import lombok.Data;

@Data
public class AuthResponseDto {
    
    private String token;
    private String type = "Bearer";
    private Long id;
    private String email;
    private String mobile;
    private User.UserRole role;
    private User.UserStatus status;
    private Boolean isVerified;
    private String message;
    
    public AuthResponseDto(String token, User user) {
        this.token = token;
        this.id = user.getId();
        this.email = user.getEmail();
        this.mobile = user.getMobile();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.isVerified = user.getIsVerified();
    }
    
    public AuthResponseDto(String message) {
        this.message = message;
    }
}
