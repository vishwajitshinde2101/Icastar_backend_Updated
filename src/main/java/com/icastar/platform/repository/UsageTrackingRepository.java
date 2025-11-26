package com.icastar.platform.repository;

import com.icastar.platform.entity.UsageTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UsageTrackingRepository extends JpaRepository<UsageTracking, Long> {
    
    // Find usage by user ID
    List<UsageTracking> findByUserId(Long userId);
    
    // Find usage by user ID and feature type
    List<UsageTracking> findByUserIdAndFeatureType(Long userId, String featureType);
    
    // Find usage by feature type
    List<UsageTracking> findByFeatureType(String featureType);
    
    // Find usage by date range
    @Query("SELECT ut FROM UsageTracking ut WHERE ut.usageDate BETWEEN :startDate AND :endDate")
    List<UsageTracking> findByUsageDateBetween(@Param("startDate") LocalDateTime startDate, 
                                              @Param("endDate") LocalDateTime endDate);
    
    // Find usage by user ID and date range
    @Query("SELECT ut FROM UsageTracking ut WHERE ut.userId = :userId AND ut.usageDate BETWEEN :startDate AND :endDate")
    List<UsageTracking> findByUserIdAndUsageDateBetween(@Param("userId") Long userId, 
                                                       @Param("startDate") LocalDateTime startDate, 
                                                       @Param("endDate") LocalDateTime endDate);
    
    // Find usage by user ID, feature type, and date range
    @Query("SELECT ut FROM UsageTracking ut WHERE ut.userId = :userId AND ut.featureType = :featureType AND ut.usageDate BETWEEN :startDate AND :endDate")
    List<UsageTracking> findByUserIdAndFeatureTypeAndUsageDateBetween(@Param("userId") Long userId, 
                                                                      @Param("featureType") String featureType, 
                                                                      @Param("startDate") LocalDateTime startDate, 
                                                                      @Param("endDate") LocalDateTime endDate);
    
    // Count usage by user ID
    Long countByUserId(Long userId);
    
    // Count usage by user ID and feature type
    Long countByUserIdAndFeatureType(Long userId, String featureType);
    
    // Count usage by feature type
    Long countByFeatureType(String featureType);
    
    // Count usage by date range
    @Query("SELECT COUNT(ut) FROM UsageTracking ut WHERE ut.usageDate BETWEEN :startDate AND :endDate")
    Long countByUsageDateBetween(@Param("startDate") LocalDateTime startDate, 
                                @Param("endDate") LocalDateTime endDate);
    
    // Get total usage value by user ID and feature type
    @Query("SELECT SUM(ut.featureValue) FROM UsageTracking ut WHERE ut.userId = :userId AND ut.featureType = :featureType")
    Double getTotalUsageByUserIdAndFeatureType(@Param("userId") Long userId, 
                                               @Param("featureType") String featureType);
    
    // Get total usage value by user ID
    @Query("SELECT SUM(ut.featureValue) FROM UsageTracking ut WHERE ut.userId = :userId")
    Double getTotalUsageByUserId(@Param("userId") Long userId);
    
    // Get usage statistics by feature type
    @Query("SELECT ut.featureType, COUNT(ut), SUM(ut.featureValue) FROM UsageTracking ut GROUP BY ut.featureType")
    List<Object[]> getUsageStatisticsByFeatureType();
    
    // Get usage statistics by user ID
    @Query("SELECT ut.featureType, COUNT(ut), SUM(ut.featureValue) FROM UsageTracking ut WHERE ut.userId = :userId GROUP BY ut.featureType")
    List<Object[]> getUsageStatisticsByUserId(@Param("userId") Long userId);
}
