package com.icastar.platform.repository;

import com.icastar.platform.entity.AccountStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccountStatusHistoryRepository extends JpaRepository<AccountStatusHistory, Long> {
    
    List<AccountStatusHistory> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    @Query("SELECT ash FROM AccountStatusHistory ash WHERE ash.user.id = :userId AND ash.status = :status ORDER BY ash.createdAt DESC")
    List<AccountStatusHistory> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") AccountStatusHistory.AccountStatus status);
    
    @Query("SELECT ash FROM AccountStatusHistory ash WHERE ash.createdAt BETWEEN :startDate AND :endDate ORDER BY ash.createdAt DESC")
    List<AccountStatusHistory> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}
