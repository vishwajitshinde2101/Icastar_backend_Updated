package com.icastar.platform.dto.castingcall;

import com.icastar.platform.entity.CastingCallApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CastingCallApplicationResponseDto {
    private Long id;
    private Long castingCallId;
    private String castingCallTitle;

    // Artist info
    private Long artistId;
    private String artistName;
    private String artistStageName;
    private String artistPhotoUrl;
    private String artistLocation;
    private Integer artistExperienceYears;

    // Application content
    private String coverLetter;
    private String auditionVideoUrl;
    private String resumeUrl;
    private String portfolioUrl;
    private String demoReelUrl;

    // Status
    private CastingCallApplication.ApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    private LocalDateTime shortlistedAt;
    private LocalDateTime callbackScheduledAt;
    private LocalDateTime callbackCompletedAt;
    private LocalDateTime selectedAt;
    private LocalDateTime rejectedAt;

    private Boolean isShortlisted;
    private Boolean isSelected;

    // Recruiter feedback (only visible to recruiter)
    private String notes;
    private Integer rating;
    private String feedback;
    private String rejectionReason;

    // Callback info
    private LocalDateTime callbackDate;
    private String callbackLocation;
    private String callbackNotes;
}
