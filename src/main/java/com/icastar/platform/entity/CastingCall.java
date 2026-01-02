package com.icastar.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "casting_calls")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"applications", "recruiter"})
@ToString(exclude = {"applications", "recruiter"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CastingCall extends BaseEntity {

    // Ownership
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;

    // Basic Information
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "requirements", columnDefinition = "TEXT")
    private String requirements;

    @Column(name = "location")
    private String location;

    @Column(name = "role_type")
    private String roleType; // "Lead", "Supporting", "Background", "Voice Over"

    @Column(name = "character_name")
    private String characterName;

    @Column(name = "project_name")
    private String projectName;

    @Column(name = "project_type")
    private String projectType; // "Film", "TV Series", "Commercial", "Theater", "Web Series"

    // Status Management
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CastingCallStatus status = CastingCallStatus.DRAFT;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Compensation
    @Column(name = "compensation_min")
    private BigDecimal compensationMin;

    @Column(name = "compensation_max")
    private BigDecimal compensationMax;

    @Column(name = "currency", length = 3)
    private String currency = "INR";

    @Column(name = "is_paid")
    private Boolean isPaid = true;

    @Column(name = "payment_terms", columnDefinition = "TEXT")
    private String paymentTerms;

    // Scheduling
    @Column(name = "audition_date")
    private LocalDateTime auditionDate;

    @Column(name = "audition_deadline")
    private LocalDate auditionDeadline;

    @Column(name = "estimated_shooting_start")
    private LocalDate estimatedShootingStart;

    @Column(name = "estimated_shooting_end")
    private LocalDate estimatedShootingEnd;

    @Column(name = "shooting_duration_days")
    private Integer shootingDurationDays;

    // Requirements
    @Column(name = "age_range_min")
    private Integer ageRangeMin;

    @Column(name = "age_range_max")
    private Integer ageRangeMax;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_preference")
    private GenderPreference genderPreference;

    @Column(name = "required_skills", columnDefinition = "JSON")
    private String requiredSkills; // JSON array ["Acting", "Dancing", "Singing"]

    @Column(name = "preferred_languages", columnDefinition = "JSON")
    private String preferredLanguages; // JSON array

    @Column(name = "physical_requirements", columnDefinition = "TEXT")
    private String physicalRequirements;

    // Flags
    @Column(name = "is_urgent")
    private Boolean isUrgent = false;

    @Column(name = "is_featured")
    private Boolean isFeatured = false;

    @Column(name = "accepts_remote_auditions")
    private Boolean acceptsRemoteAuditions = false;

    @Column(name = "requires_video_audition")
    private Boolean requiresVideoAudition = false;

    // Counters (denormalized for performance)
    @Column(name = "applications_count")
    private Integer applicationsCount = 0;

    @Column(name = "views_count")
    private Integer viewsCount = 0;

    @Column(name = "shortlisted_count")
    private Integer shortlistedCount = 0;

    @Column(name = "selected_count")
    private Integer selectedCount = 0;

    // Additional Information
    @Column(name = "audition_format")
    @Enumerated(EnumType.STRING)
    private AuditionFormat auditionFormat;

    @Column(name = "audition_location")
    private String auditionLocation;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    // Relationships
    @OneToMany(mappedBy = "castingCall", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CastingCallApplication> applications;

    // Enums
    public enum CastingCallStatus {
        DRAFT,      // Created but not published
        OPEN,       // Published and accepting applications
        CLOSED,     // Closed for new applications
        CANCELLED   // Cancelled by recruiter
    }

    public enum GenderPreference {
        MALE, FEMALE, NON_BINARY, ANY
    }

    public enum AuditionFormat {
        IN_PERSON, VIDEO_SUBMISSION, VIRTUAL_LIVE, HYBRID
    }
}
