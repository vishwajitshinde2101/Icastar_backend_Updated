package com.icastar.platform.repository;

import com.icastar.platform.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    
    // Find active subscription by user ID
    Optional<Subscription> findByUserIdAndStatus(Long userId, Subscription.SubscriptionStatus status);
    
    // Find all subscriptions by user ID
    List<Subscription> findByUserId(Long userId);
    
    // Find subscriptions by status
    List<Subscription> findByStatus(Subscription.SubscriptionStatus status);
    
    // Find subscriptions expiring soon
    @Query("SELECT s FROM Subscription s WHERE s.expiresAt BETWEEN :startDate AND :endDate")
    List<Subscription> findExpiringSubscriptions(@Param("startDate") LocalDateTime startDate, 
                                                @Param("endDate") LocalDateTime endDate);
    
    // Find expired subscriptions
    @Query("SELECT s FROM Subscription s WHERE s.expiresAt < :currentDate AND s.status = 'ACTIVE'")
    List<Subscription> findExpiredSubscriptions(@Param("currentDate") LocalDateTime currentDate);
    
    // Count active subscriptions
    Long countByStatus(Subscription.SubscriptionStatus status);
    
    // Find subscriptions by plan
    List<Subscription> findBySubscriptionPlanId(Long planId);
    
    // Find subscriptions by user role
    @Query("SELECT s FROM Subscription s WHERE s.subscriptionPlan.userRole = :userRole")
    List<Subscription> findByUserRole(@Param("userRole") String userRole);
    
    // Find subscriptions by plan type
    @Query("SELECT s FROM Subscription s WHERE s.subscriptionPlan.planType = :planType")
    List<Subscription> findByPlanType(@Param("planType") String planType);
}
