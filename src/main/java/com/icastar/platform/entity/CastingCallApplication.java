package com.icastar.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "casting_call_applications",
       uniqueConstraints = @UniqueConstraint(columnNames = {"casting_call_id", "artist_id"}))
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"castingCall", "artist"})
@ToString(exclude = {"castingCall", "artist"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class CastingCallApplication extends BaseEntity {

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casting_call_id", nullable = false)
    private CastingCall castingCall;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistProfile artist;

    // Application Content
    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Column(name = "audition_video_url")
    private String auditionVideoUrl; // S3 URL

    @Column(name = "resume_url")
    private String resumeUrl; // S3 URL

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Column(name = "demo_reel_url")
    private String demoReelUrl;

    // Status Management
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "shortlisted_at")
    private LocalDateTime shortlistedAt;

    @Column(name = "callback_scheduled_at")
    private LocalDateTime callbackScheduledAt;

    @Column(name = "callback_completed_at")
    private LocalDateTime callbackCompletedAt;

    @Column(name = "selected_at")
    private LocalDateTime selectedAt;

    @Column(name = "rejected_at")
    private LocalDateTime rejectedAt;

    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    // Flags
    @Column(name = "is_shortlisted")
    private Boolean isShortlisted = false;

    @Column(name = "is_selected")
    private Boolean isSelected = false;

    // Recruiter Feedback (Private)
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // Private recruiter notes

    @Column(name = "rating")
    private Integer rating; // 1-5 stars

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback; // Internal feedback

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    // Callback Information
    @Column(name = "callback_date")
    private LocalDateTime callbackDate;

    @Column(name = "callback_location")
    private String callbackLocation;

    @Column(name = "callback_notes", columnDefinition = "TEXT")
    private String callbackNotes;

    // Enums
    public enum ApplicationStatus {
        APPLIED,              // Initial application submitted
        UNDER_REVIEW,         // Being reviewed by recruiter
        SHORTLISTED,          // Selected for callback/further consideration
        CALLBACK_SCHEDULED,   // Callback audition scheduled
        CALLBACK_COMPLETED,   // Callback audition completed
        SELECTED,             // Selected for the role
        REJECTED,             // Application rejected
        WITHDRAWN             // Artist withdrew application
    }
}
