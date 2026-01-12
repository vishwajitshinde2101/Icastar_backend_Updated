package com.icastar.platform.repository;

import com.icastar.platform.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Total artist earnings (COMMISSION payments only)
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.user.id = :userId AND p.paymentType = 'COMMISSION' AND p.status = 'SUCCESS'")
    BigDecimal sumEarningsByUserId(@Param("userId") Long userId);

    // Monthly earnings for artist
    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.user.id = :userId AND p.paymentType = 'COMMISSION' AND p.status = 'SUCCESS' AND p.paidAt BETWEEN :startDate AND :endDate")
    BigDecimal sumEarningsByUserIdAndMonth(@Param("userId") Long userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    // Count total earnings transactions
    @Query("SELECT COUNT(p) FROM Payment p WHERE p.user.id = :userId AND p.paymentType = 'COMMISSION' AND p.status = 'SUCCESS'")
    Long countEarningsByUserId(@Param("userId") Long userId);
}
