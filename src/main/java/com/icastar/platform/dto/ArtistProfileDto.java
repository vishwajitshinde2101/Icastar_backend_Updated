package com.icastar.platform.dto;

import com.icastar.platform.entity.ArtistProfile;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class ArtistProfileDto {
    private Long id;
    private Long userId;
    private Long artistTypeId;
    private String artistTypeName;
    private String firstName;
    private String lastName;
    private String stageName;
    private String bio;
    private LocalDate dateOfBirth;
    private ArtistProfile.Gender gender;
    private String location;
    private String profileImageUrl;
    private List<String> portfolioUrls;
    private List<String> skills;
    private Integer experienceYears;
    private Double hourlyRate;
    private Boolean isVerifiedBadge;
    private LocalDate verificationRequestedAt;
    private LocalDate verificationApprovedAt;
    private Integer totalApplications;
    private Integer successfulHires;
    private Boolean isProfileComplete;
    private List<ArtistProfileFieldDto> dynamicFields;
}
