package com.icastar.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "subscriptions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Subscription {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SubscriptionStatus status;
    
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;
    
    @Column(name = "end_date")
    private LocalDateTime endDate;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "auto_renew", nullable = false)
    @Builder.Default
    private Boolean autoRenew = false;
    
    @Column(name = "amount_paid", precision = 10, scale = 2)
    private BigDecimal amountPaid;
    
    @Column(name = "payment_reference", length = 255)
    private String paymentReference;
    
    // Usage tracking
    @Column(name = "auditions_used")
    @Builder.Default
    private Integer auditionsUsed = 0;
    
    @Column(name = "applications_used")
    @Builder.Default
    private Integer applicationsUsed = 0;
    
    @Column(name = "messages_used")
    @Builder.Default
    private Integer messagesUsed = 0;
    
    @Column(name = "candidates_viewed")
    @Builder.Default
    private Integer candidatesViewed = 0;
    
    @Column(name = "job_boosts_used")
    @Builder.Default
    private Integer jobBoostsUsed = 0;
    
    @Column(name = "file_uploads_used")
    @Builder.Default
    private Integer fileUploadsUsed = 0;
    
    // Trial information
    @Column(name = "is_trial", nullable = false)
    @Builder.Default
    private Boolean isTrial = false;
    
    @Column(name = "trial_end_date")
    private LocalDateTime trialEndDate;
    
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
    @OneToMany(mappedBy = "subscription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UsageTracking> usageTracking;
    
    // Enums
    public enum SubscriptionStatus {
        ACTIVE, EXPIRED, CANCELLED, SUSPENDED, TRIAL
    }
}