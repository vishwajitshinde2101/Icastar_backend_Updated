package com.icastar.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
@Data
@EqualsAndHashCode(callSuper = true)
public class Otp extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    @Column(name = "mobile", nullable = false)
    private String mobile;

    @Column(name = "email")
    private String email;

    @Column(name = "otp_code", nullable = false)
    private String otpCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "otp_type", nullable = false)
    private OtpType otpType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OtpStatus status = OtpStatus.PENDING;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "attempts", nullable = false)
    private Integer attempts = 0;

    @Column(name = "max_attempts", nullable = false)
    private Integer maxAttempts = 3;

    public enum OtpType {
        LOGIN, REGISTRATION, FORGOT_PASSWORD, VERIFICATION
    }

    public enum OtpStatus {
        PENDING, VERIFIED, EXPIRED, FAILED
    }
}
