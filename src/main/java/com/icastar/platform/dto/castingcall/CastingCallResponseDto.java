package com.icastar.platform.dto.castingcall;

import com.icastar.platform.entity.CastingCall;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CastingCallResponseDto {
    private Long id;
    private String title;
    private String description;
    private String requirements;
    private String location;
    private String roleType;
    private String characterName;
    private String projectName;
    private String projectType;

    private CastingCall.CastingCallStatus status;
    private LocalDateTime publishedAt;
    private LocalDateTime closedAt;

    private BigDecimal compensationMin;
    private BigDecimal compensationMax;
    private String currency;
    private Boolean isPaid;
    private String paymentTerms;

    private LocalDateTime auditionDate;
    private LocalDate auditionDeadline;
    private LocalDate estimatedShootingStart;
    private LocalDate estimatedShootingEnd;
    private Integer shootingDurationDays;

    private Integer ageRangeMin;
    private Integer ageRangeMax;
    private CastingCall.GenderPreference genderPreference;
    private String requiredSkills;
    private String preferredLanguages;
    private String physicalRequirements;

    private Boolean isUrgent;
    private Boolean isFeatured;
    private Boolean acceptsRemoteAuditions;
    private Boolean requiresVideoAudition;

    private CastingCall.AuditionFormat auditionFormat;
    private String auditionLocation;
    private String contactEmail;
    private String contactPhone;
    private String additionalNotes;

    private Integer applicationsCount;
    private Integer viewsCount;
    private Integer shortlistedCount;
    private Integer selectedCount;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Recruiter info
    private Long recruiterId;
    private String recruiterName;
    private String recruiterCompany;
}
