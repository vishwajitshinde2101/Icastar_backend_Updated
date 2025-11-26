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
@Table(name = "subscription_plans")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SubscriptionPlan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "plan_type", nullable = false)
    private PlanType planType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false)
    private UserRole userRole;
    
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "billing_cycle", nullable = false)
    private BillingCycle billingCycle;
    
    // Artist-specific features
    @Column(name = "max_auditions")
    private Integer maxAuditions;
    
    @Column(name = "unlimited_auditions", nullable = false)
    @Builder.Default
    private Boolean unlimitedAuditions = false;
    
    @Column(name = "max_applications")
    private Integer maxApplications;
    
    @Column(name = "unlimited_applications", nullable = false)
    @Builder.Default
    private Boolean unlimitedApplications = false;
    
    @Column(name = "max_portfolio_items")
    private Integer maxPortfolioItems;
    
    @Column(name = "unlimited_portfolio", nullable = false)
    @Builder.Default
    private Boolean unlimitedPortfolio = false;
    
    @Column(name = "profile_verification", nullable = false)
    @Builder.Default
    private Boolean profileVerification = false;
    
    @Column(name = "priority_verification", nullable = false)
    @Builder.Default
    private Boolean priorityVerification = false;
    
    @Column(name = "featured_profile", nullable = false)
    @Builder.Default
    private Boolean featuredProfile = false;
    
    @Column(name = "advanced_analytics", nullable = false)
    @Builder.Default
    private Boolean advancedAnalytics = false;
    
    // Recruiter-specific features
    @Column(name = "max_messages")
    private Integer maxMessages;
    
    @Column(name = "unlimited_messages", nullable = false)
    @Builder.Default
    private Boolean unlimitedMessages = false;
    
    @Column(name = "max_candidates_view")
    private Integer maxCandidatesView;
    
    @Column(name = "unlimited_candidates", nullable = false)
    @Builder.Default
    private Boolean unlimitedCandidates = false;
    
    @Column(name = "job_boost_credits")
    @Builder.Default
    private Integer jobBoostCredits = 0;
    
    @Column(name = "advanced_search", nullable = false)
    @Builder.Default
    private Boolean advancedSearch = false;
    
    @Column(name = "candidate_verification", nullable = false)
    @Builder.Default
    private Boolean candidateVerification = false;
    
    @Column(name = "priority_support", nullable = false)
    @Builder.Default
    private Boolean prioritySupport = false;
    
    // Common features
    @Column(name = "max_file_uploads")
    private Integer maxFileUploads;
    
    @Column(name = "unlimited_uploads", nullable = false)
    @Builder.Default
    private Boolean unlimitedUploads = false;
    
    @Column(name = "max_file_size_mb")
    @Builder.Default
    private Integer maxFileSizeMb = 10;
    
    @Column(name = "custom_branding", nullable = false)
    @Builder.Default
    private Boolean customBranding = false;
    
    @Column(name = "api_access", nullable = false)
    @Builder.Default
    private Boolean apiAccess = false;
    
    @Column(name = "white_label", nullable = false)
    @Builder.Default
    private Boolean whiteLabel = false;
    
    // Plan metadata
    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;
    
    @Column(name = "is_popular", nullable = false)
    @Builder.Default
    private Boolean isPopular = false;
    
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToMany(mappedBy = "subscriptionPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Subscription> subscriptions;
    
    @OneToMany(mappedBy = "subscriptionPlan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PlanFeature> features;
    
    // Enums
    public enum PlanType {
        FREE, BASIC, PREMIUM, PROFESSIONAL, ENTERPRISE
    }
    
    public enum UserRole {
        ARTIST, RECRUITER, BOTH
    }
    
    public enum BillingCycle {
        MONTHLY, YEARLY, ONE_TIME
    }
}