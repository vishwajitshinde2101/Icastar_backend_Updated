package com.icastar.platform.repository;

import com.icastar.platform.entity.AccountManagementLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccountManagementLogRepository extends JpaRepository<AccountManagementLog, Long> {
    
    List<AccountManagementLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    List<AccountManagementLog> findByAdminIdOrderByCreatedAtDesc(Long adminId);
    
    Page<AccountManagementLog> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    @Query("SELECT aml FROM AccountManagementLog aml WHERE aml.user.id = :userId AND aml.action = :action ORDER BY aml.createdAt DESC")
    List<AccountManagementLog> findByUserIdAndAction(@Param("userId") Long userId, @Param("action") AccountManagementLog.AccountAction action);
    
    @Query("SELECT aml FROM AccountManagementLog aml WHERE aml.createdAt BETWEEN :startDate AND :endDate ORDER BY aml.createdAt DESC")
    List<AccountManagementLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT aml FROM AccountManagementLog aml WHERE aml.admin.id = :adminId AND aml.createdAt BETWEEN :startDate AND :endDate ORDER BY aml.createdAt DESC")
    List<AccountManagementLog> findByAdminAndDateRange(@Param("adminId") Long adminId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    Long countByCreatedAtAfter(LocalDateTime date);
}
