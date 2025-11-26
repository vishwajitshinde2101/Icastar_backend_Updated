package com.icastar.platform.repository;

import com.icastar.platform.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp, Long> {

    Optional<Otp> findByMobileAndEmailAndOtpTypeAndStatus(String mobile, String email, 
                                                          Otp.OtpType otpType, Otp.OtpStatus status);

    List<Otp> findByMobileAndEmailAndStatus(String mobile, String email, Otp.OtpStatus status);

    List<Otp> findByExpiresAtBeforeAndStatus(LocalDateTime expiresAt, Otp.OtpStatus status);

    @Query("SELECT o FROM Otp o WHERE o.mobile = :mobile AND o.status = :status ORDER BY o.createdAt DESC")
    List<Otp> findByMobileAndStatusOrderByCreatedAtDesc(@Param("mobile") String mobile, 
                                                        @Param("status") Otp.OtpStatus status);

    @Query("SELECT o FROM Otp o WHERE o.email = :email AND o.status = :status ORDER BY o.createdAt DESC")
    List<Otp> findByEmailAndStatusOrderByCreatedAtDesc(@Param("email") String email, 
                                                       @Param("status") Otp.OtpStatus status);
}
