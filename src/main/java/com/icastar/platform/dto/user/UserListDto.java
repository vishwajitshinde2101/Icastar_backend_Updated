package com.icastar.platform.dto.user;

import com.icastar.platform.entity.User;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserListDto {
    private Long id;
    private String email;
    private String mobile;
    private User.UserRole role;
    private User.UserStatus status;
    private Boolean isVerified;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    private String firstName;
    private String lastName;

    public UserListDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.mobile = user.getMobile();
        this.role = user.getRole();
        this.status = user.getStatus();
        this.isVerified = user.getIsVerified();
        this.lastLogin = user.getLastLogin();
        this.createdAt = user.getCreatedAt();
        
        // Set profile-specific fields based on role
        if (user.getRole() == User.UserRole.ARTIST && user.getArtistProfile() != null) {
            this.firstName = user.getArtistProfile().getFirstName();
            this.lastName = user.getArtistProfile().getLastName();
        } else if (user.getRole() == User.UserRole.RECRUITER && user.getRecruiterProfile() != null) {
            this.firstName = user.getRecruiterProfile().getContactPersonName();
            this.lastName = user.getRecruiterProfile().getCompanyName();
        }
    }
}
