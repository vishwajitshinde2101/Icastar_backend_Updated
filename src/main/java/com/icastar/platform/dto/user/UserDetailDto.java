package com.icastar.platform.dto.user;

import com.icastar.platform.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDetailDto {
    private Long id;
    private String email;
    private String mobile;
    private User.UserRole role;
    private User.UserStatus status;
    private Boolean isVerified;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Integer failedLoginAttempts;
    private LocalDateTime accountLockedUntil;
    
    // Profile information
    private Object profile; // Will be ArtistProfileDto or RecruiterProfileDto
    
    // Subscription information
    private Object subscription; // Will be SubscriptionDto
    
    // Statistics
    private Long totalApplications;
    private Long totalJobsPosted;
    private Long successfulHires;
    private Integer chatCredits;

    public UserDetailDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.mobile = user.getMobile();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.isVerified = user.getIsVerified();
        this.lastLogin = user.getLastLogin();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
        this.failedLoginAttempts = user.getFailedLoginAttempts();
        this.accountLockedUntil = user.getAccountLockedUntil();
        
        // Set profile-specific statistics
        if (user.getRole() == User.UserRole.ARTIST && user.getArtistProfile() != null) {
            this.totalApplications = user.getArtistProfile().getTotalApplications().longValue();
            this.successfulHires = user.getArtistProfile().getSuccessfulHires().longValue();
        } else if (user.getRole() == User.UserRole.RECRUITER && user.getRecruiterProfile() != null) {
            this.totalJobsPosted = user.getRecruiterProfile().getTotalJobsPosted().longValue();
            this.successfulHires = user.getRecruiterProfile().getSuccessfulHires().longValue();
            this.chatCredits = user.getRecruiterProfile().getChatCredits();
        }
    }
}
