package com.icastar.platform.dto.castingcall;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icastar.platform.entity.CastingCall;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCastingCallDto {
    // All fields optional for partial updates
    private String title;
    private String description;
    private String requirements;
    private String location;
    private String roleType;
    private String characterName;
    private String projectName;
    private String projectType;

    private BigDecimal compensationMin;
    private BigDecimal compensationMax;
    private String currency;
    private Boolean isPaid;
    private String paymentTerms;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime auditionDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate auditionDeadline;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate estimatedShootingStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
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
}
