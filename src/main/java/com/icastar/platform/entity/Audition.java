package com.icastar.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "auditions")
@Data
@EqualsAndHashCode(callSuper = true)
public class Audition extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_application_id", nullable = true)
    private JobApplication jobApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private RecruiterProfile recruiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = true)
    private ArtistProfile artist;

    // Target artist type for role-based filtering (for open auditions)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_artist_type_id", nullable = true)
    private ArtistType targetArtistType;

    // Title for the audition (for open auditions)
    @Column(name = "title")
    private String title;

    // Description for the audition
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Flag to indicate if this is an open audition (any matching artist can apply)
    @Column(name = "is_open_audition")
    private Boolean isOpenAudition = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "audition_type", nullable = false)
    private AuditionType auditionType;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Column(name = "meeting_link")
    private String meetingLink;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AuditionStatus status = AuditionStatus.SCHEDULED;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "rating")
    private Integer rating; // 1-5 stars

    @Column(name = "recording_url")
    private String recordingUrl;

    public enum AuditionType {
        LIVE_VIDEO, LIVE_AUDIO, RECORDED_SUBMISSION, IN_PERSON
    }

    public enum AuditionStatus {
        SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW
    }
}
