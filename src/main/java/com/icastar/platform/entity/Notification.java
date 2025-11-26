package com.icastar.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@EqualsAndHashCode(callSuper = true)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority = Priority.MEDIUM;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "action_url")
    private String actionUrl;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata; // JSON for additional data

    @Column(name = "email_sent", nullable = false)
    private Boolean emailSent = false;

    @Column(name = "push_sent", nullable = false)
    private Boolean pushSent = false;

    public enum NotificationType {
        JOB_ALERT, APPLICATION_RECEIVED, AUDITION_SCHEDULED, PAYMENT_SUCCESS, 
        SUBSCRIPTION_EXPIRY, VERIFICATION_APPROVED, MESSAGE_RECEIVED, SYSTEM_UPDATE
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
}
