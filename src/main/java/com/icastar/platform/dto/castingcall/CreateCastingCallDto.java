package com.icastar.platform.dto.castingcall;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.icastar.platform.entity.CastingCall;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCastingCallDto {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String requirements;
    private String location;
    private String roleType;
    private String characterName;
    private String projectName;
    private String projectType;

    // Compensation
    private BigDecimal compensationMin;
    private BigDecimal compensationMax;
    private String currency = "INR";
    private Boolean isPaid = true;
    private String paymentTerms;

    // Scheduling
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime auditionDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate auditionDeadline;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate estimatedShootingStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate estimatedShootingEnd;

    private Integer shootingDurationDays;

    // Requirements
    @Min(value = 0, message = "Minimum age must be at least 0")
    @Max(value = 150, message = "Maximum age must be at most 150")
    private Integer ageRangeMin;

    @Min(value = 0, message = "Minimum age must be at least 0")
    @Max(value = 150, message = "Maximum age must be at most 150")
    private Integer ageRangeMax;

    private CastingCall.GenderPreference genderPreference;
    private String requiredSkills; // JSON string
    private String preferredLanguages; // JSON string
    private String physicalRequirements;

    // Flags
    private Boolean isUrgent = false;
    private Boolean isFeatured = false;
    private Boolean acceptsRemoteAuditions = false;
    private Boolean requiresVideoAudition = false;

    // Audition details
    private CastingCall.AuditionFormat auditionFormat;
    private String auditionLocation;
    private String contactEmail;
    private String contactPhone;
    private String additionalNotes;
}
