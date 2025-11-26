package com.icastar.platform.dto.user;

import com.icastar.platform.entity.User;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProfileDto {
    
    private Long id;
    private String email;
    private String mobile;
    private User.UserRole role;
    private User.UserStatus status;
    private Boolean isVerified;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Profile specific fields
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private String bio;
    private String location;
    
    public UserProfileDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.mobile = user.getMobile();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.isVerified = user.getIsVerified();
        this.lastLogin = user.getLastLogin();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }
}
