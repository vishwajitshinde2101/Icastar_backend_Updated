package com.icastar.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Data
@EqualsAndHashCode(callSuper = true)
public class Message extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_id")
    private RecruiterProfile recruiter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id")
    private ArtistProfile artist;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.TEXT;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    @Column(name = "attachment_url")
    private String attachmentUrl;

    @Column(name = "is_paid_message", nullable = false)
    private Boolean isPaidMessage = false;

    @Column(name = "payment_amount")
    private Double paymentAmount;

    @Column(name = "payment_reference")
    private String paymentReference;

    @Column(name = "is_deleted_by_sender", nullable = false)
    private Boolean isDeletedBySender = false;

    @Column(name = "is_deleted_by_recipient", nullable = false)
    private Boolean isDeletedByRecipient = false;

    public enum MessageType {
        TEXT, IMAGE, FILE, AUDIO, VIDEO
    }
}
