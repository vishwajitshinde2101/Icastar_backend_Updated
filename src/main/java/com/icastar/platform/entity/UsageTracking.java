package com.icastar.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "usage_tracking")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UsageTracking {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "user_id", nullable = false, insertable = false, updatable = false)
    private Long userId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription;
    
    @Column(name = "feature_name", nullable = false, length = 100)
    private String featureName;
    
    @Column(name = "feature_type", nullable = false, length = 100)
    private String featureType;
    
    @Column(name = "feature_value")
    private Double featureValue;
    
    @Column(name = "usage_count")
    @Builder.Default
    private Integer usageCount = 1;
    
    @Column(name = "usage_date", nullable = false)
    @Builder.Default
    private LocalDateTime usageDate = LocalDateTime.now();
    
    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
