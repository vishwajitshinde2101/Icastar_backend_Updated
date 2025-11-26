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
import java.util.List;

@Entity
@Table(name = "recruiter_category_fields")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class RecruiterCategoryField {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruiter_category_id", nullable = false)
    private RecruiterCategory recruiterCategory;
    
    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;
    
    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    private FieldType fieldType;
    
    @Column(name = "is_required", nullable = false)
    @Builder.Default
    private Boolean isRequired = false;
    
    @Column(name = "is_searchable", nullable = false)
    @Builder.Default
    private Boolean isSearchable = false;
    
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
    
    @Column(name = "validation_rules", columnDefinition = "JSON")
    private String validationRules;
    
    @Column(name = "options", columnDefinition = "JSON")
    private String options;
    
    @Column(name = "placeholder", length = 255)
    private String placeholder;
    
    @Column(name = "help_text", length = 500)
    private String helpText;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "recruiterCategoryField", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RecruiterProfileField> profileFields;
}
