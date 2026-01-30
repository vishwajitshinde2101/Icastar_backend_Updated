package com.icastar.platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "hire_requests")
@Data
@EqualsAndHashCode(callSuper = true, exclude = {"recruiter", "artist", "job"})
@ToString(exclude = {"recruiter", "artist", "job"})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class HireRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistProfile artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private HireRequestStatus status = HireRequestStatus.PENDING;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "viewed_at")
    private LocalDateTime viewedAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "hired_at")
    private LocalDateTime hiredAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "artist_response", columnDefinition = "TEXT")
    private String artistResponse;

    @Column(name = "email_sent", nullable = false)
    private Boolean emailSent = false;

    @Column(name = "reminder_sent", nullable = false)
    private Boolean reminderSent = false;

    @Column(name = "reminder_sent_at")
    private LocalDateTime reminderSentAt;

    @Column(name = "offered_salary")
    private Double offeredSalary;

    @Column(name = "project_details", columnDefinition = "TEXT")
    private String projectDetails;

    public enum HireRequestStatus {
        PENDING,        // Initial state - sent to artist
        VIEWED,         // Artist has viewed the request
        ACCEPTED,       // Artist accepted the offer
        DECLINED,       // Artist declined the offer
        HIRED,          // Successfully hired
        WITHDRAWN,      // Recruiter withdrew the request
        EXPIRED         // Request expired without response
    }
}