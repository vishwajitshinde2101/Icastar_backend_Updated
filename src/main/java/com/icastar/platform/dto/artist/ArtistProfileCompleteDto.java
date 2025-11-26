package com.icastar.platform.dto.artist;

import com.icastar.platform.dto.ArtistProfileFieldDto;
import com.icastar.platform.entity.ArtistProfile;
import com.icastar.platform.entity.Document;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArtistProfileCompleteDto {
    
    // User fields
    private Long userId;
    private String email;
    private String firstName;
    private String lastName;
    private String phone;
    private String city;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Artist Profile fields
    private Long artistProfileId;
    private Long artistTypeId;
    private String artistTypeName;
    private String stageName;
    private String bio;
    private LocalDate dateOfBirth;
    private ArtistProfile.Gender gender;
    private String location;
    private ArtistProfile.MaritalStatus maritalStatus;
    private String languagesSpoken; // JSON string
    private String comfortableAreas; // JSON string
    private String projectsWorked; // JSON string
    private String skills; // JSON string
    private Integer experienceYears;
    private Double weight;
    private Double height;
    private String hairColor;
    private String hairLength;
    private Boolean hasTattoo;
    private Boolean hasMole;
    private String shoeSize;
    private String travelCities; // JSON string
    private Double hourlyRate;
    private Boolean isVerifiedBadge;
    private LocalDate verificationRequestedAt;
    private LocalDate verificationApprovedAt;
    private Integer totalApplications;
    private Integer successfulHires;
    private Boolean isProfileComplete;
    
    // Documents
    private List<DocumentDto> documents;
    
    // Dynamic fields
    private List<ArtistProfileFieldDto> dynamicFields;
    
    @Data
    public static class DocumentDto {
        private Long id;
        private Document.DocumentType documentType;
        private String fileName;
        private String fileUrl;
        private Long fileSize;
        private String mimeType;
        private LocalDateTime uploadedAt;
        private Boolean isVerified;
        private LocalDateTime verifiedAt;
        private Long verifiedBy;
        private String verificationNotes;
    }
}
