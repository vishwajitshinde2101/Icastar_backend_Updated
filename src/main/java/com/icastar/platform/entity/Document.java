package com.icastar.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@EqualsAndHashCode(callSuper = true)
public class Document extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private DocumentType documentType;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "file_url", nullable = false)
    private String fileUrl; // S3 URL

    @Column(name = "file_size")
    private Long fileSize; // in bytes

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    @Column(name = "is_verified", nullable = false)
    private Boolean isVerified = false;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verified_by")
    private Long verifiedBy; // Admin user ID who verified

    @Column(name = "verification_notes", columnDefinition = "TEXT")
    private String verificationNotes;

    public enum DocumentType {
        PROFILE_ID,           // Main profile image
        PROFILE_LEFT,         // Left profile picture
        PROFILE_RIGHT,        // Right profile picture
        PROFILE_FRONT,        // Front profile picture
        PAN,                  // PAN card
        AADHAR,               // Aadhar card
        PASSPORT,             // Passport
        ID_SIZE_PIC,          // ID size photograph
        ACTING_VIDEO,         // Acting video (9 ras)
        PORTFOLIO_IMAGE,      // Portfolio images
        HEADSHOT,             // Professional headshots
        DEMO_REEL,            // Demo reel video
        RESUME,               // Acting resume/CV
        OTHER                 // Other documents
    }
}
