package com.icastar.platform.dto.recruiter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArtistSuggestionDto {
    
    private Long artistId;
    private String artistName;
    private String artistEmail;
    private String artistCategory;
    private String artistType;
    private String location;
    private String bio;
    private String profilePhoto;
    
    // Match Score and Reasons
    private Double matchScore;
    private List<String> matchReasons;
    private List<String> skills;
    private List<String> genres;
    private List<String> languages;
    
    // Experience and Portfolio
    private Integer experienceYears;
    private String experienceLevel;
    private List<String> portfolioItems;
    private List<String> achievements;
    private List<String> certifications;
    
    // Availability and Preferences
    private String availability;
    private String preferredJobType;
    private BigDecimal expectedSalaryMin;
    private BigDecimal expectedSalaryMax;
    private String currency;
    private String workLocation;
    private String workSchedule;
    
    // Contact and Social
    private String phone;
    private String website;
    private List<String> socialLinks;
    private String contactPreference;
    
    // Platform Activity
    private LocalDateTime lastActive;
    private Integer totalApplications;
    private Integer totalHires;
    private Double hireRate;
    private String verificationStatus;
    private Boolean isVerified;
    private Boolean isPremium;
    
    // Quick Actions
    private Boolean canViewProfile;
    private Boolean canContact;
    private Boolean canShortlist;
    private Boolean canInvite;
    private Boolean canMessage;
}
