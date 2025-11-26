package com.icastar.platform.repository;

import com.icastar.platform.entity.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    
    // Find plans by user role
    List<SubscriptionPlan> findByUserRole(SubscriptionPlan.UserRole userRole);
    
    // Find plans by plan type
    List<SubscriptionPlan> findByPlanType(SubscriptionPlan.PlanType planType);
    
    // Find active plans
    List<SubscriptionPlan> findByIsActiveTrue();
    
    // Find plans by user role and plan type
    List<SubscriptionPlan> findByUserRoleAndPlanType(SubscriptionPlan.UserRole userRole, 
                                                     SubscriptionPlan.PlanType planType);
    
    // Find plans by user role and active status
    List<SubscriptionPlan> findByUserRoleAndIsActiveTrue(SubscriptionPlan.UserRole userRole);
    
    // Find plans by plan type and active status
    List<SubscriptionPlan> findByPlanTypeAndIsActiveTrue(SubscriptionPlan.PlanType planType);
    
    // Find plans by price range
    @Query("SELECT sp FROM SubscriptionPlan sp WHERE sp.price BETWEEN :minPrice AND :maxPrice")
    List<SubscriptionPlan> findByPriceRange(@Param("minPrice") Double minPrice, 
                                           @Param("maxPrice") Double maxPrice);
    
    // Find plans by name
    List<SubscriptionPlan> findByNameContainingIgnoreCase(String name);
    
    // Find plans by description
    List<SubscriptionPlan> findByDescriptionContainingIgnoreCase(String description);
    
    // Count plans by user role
    Long countByUserRole(SubscriptionPlan.UserRole userRole);
    
    // Count plans by plan type
    Long countByPlanType(SubscriptionPlan.PlanType planType);
    
    // Count active plans
    Long countByIsActiveTrue();
}
