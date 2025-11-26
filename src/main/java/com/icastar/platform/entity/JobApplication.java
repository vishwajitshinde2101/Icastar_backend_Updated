package com.icastar.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"job", "artist"})
@ToString(exclude = {"job", "artist"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class JobApplication extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistProfile artist;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.APPLIED;

    @Column(name = "cover_letter", columnDefinition = "TEXT")
    private String coverLetter;

    @Column(name = "expected_salary")
    private Double expectedSalary;

    @Column(name = "availability_date")
    private java.time.LocalDate availabilityDate;

    @Column(name = "portfolio_url")
    private String portfolioUrl;

    @Column(name = "resume_url")
    private String resumeUrl;

    @Column(name = "demo_reel_url")
    private String demoReelUrl;

    @Column(name = "applied_at", nullable = false)
    private LocalDateTime appliedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "interview_scheduled_at")
    private LocalDateTime interviewScheduledAt;

    @Column(name = "interview_notes", columnDefinition = "TEXT")
    private String interviewNotes;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;

    @Column(name = "rating")
    private Integer rating; // 1-5 stars

    @Column(name = "is_shortlisted")
    private Boolean isShortlisted = false;

    @Column(name = "is_hired")
    private Boolean isHired = false;

    @Column(name = "hired_at")
    private LocalDateTime hiredAt;

    @Column(name = "contract_url")
    private String contractUrl;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes; // Internal notes for recruiter

    public enum ApplicationStatus {
        APPLIED,           // Initial application
        UNDER_REVIEW,      // Being reviewed by recruiter
        SHORTLISTED,       // Selected for next round
        INTERVIEW_SCHEDULED, // Interview scheduled
        INTERVIEWED,       // Interview completed
        SELECTED,          // Selected for the role
        REJECTED,          // Application rejected
        WITHDRAWN,         // Candidate withdrew
        HIRED,             // Successfully hired
        ON_HOLD            // Application on hold
    }
}