package com.icastar.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "communication_logs")
@Data
@EqualsAndHashCode(callSuper = true)
public class CommunicationLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "communication_type", nullable = false)
    private CommunicationType communicationType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CommunicationStatus status;

    @Column(name = "recipient_email")
    private String recipientEmail;

    @Column(name = "recipient_mobile")
    private String recipientMobile;

    @Column(name = "subject")
    private String subject;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "failed_at")
    private LocalDateTime failedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "max_retries")
    private Integer maxRetries = 3;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    @Column(name = "external_id")
    private String externalId; // For tracking with email/SMS providers

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Long userId;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON string for additional data

    public enum CommunicationType {
        EMAIL, SMS, PUSH_NOTIFICATION, IN_APP_NOTIFICATION
    }

    public enum CommunicationStatus {
        PENDING, SENT, DELIVERED, FAILED, RETRYING, CANCELLED
    }

    // Helper methods
    public boolean isRetryable() {
        return status == CommunicationStatus.FAILED && 
               retryCount < maxRetries && 
               (nextRetryAt == null || nextRetryAt.isBefore(LocalDateTime.now()));
    }

    public void incrementRetryCount() {
        this.retryCount++;
        this.nextRetryAt = LocalDateTime.now().plusMinutes(5 * retryCount); // Exponential backoff
    }

    public void markAsSent() {
        this.status = CommunicationStatus.SENT;
        this.sentAt = LocalDateTime.now();
    }

    public void markAsDelivered() {
        this.status = CommunicationStatus.DELIVERED;
        this.deliveredAt = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = CommunicationStatus.FAILED;
        this.failedAt = LocalDateTime.now();
        this.errorMessage = errorMessage;
    }
}
