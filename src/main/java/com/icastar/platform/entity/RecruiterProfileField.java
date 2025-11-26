package com.icastar.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "recruiter_profile_fields")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RecruiterProfileField {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_profile_id", nullable = false)
    private RecruiterProfile recruiterProfile;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_category_field_id", nullable = false)
    private RecruiterCategoryField recruiterCategoryField;
    
    @Column(name = "field_value", columnDefinition = "TEXT")
    private String fieldValue;
    
    @Column(name = "file_url", length = 500)
    private String fileUrl;
    
    @Column(name = "file_name", length = 255)
    private String fileName;
    
    @Column(name = "file_size")
    private Long fileSize;
    
    @Column(name = "mime_type", length = 100)
    private String mimeType;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
