package com.icastar.platform.service;

import com.icastar.platform.entity.Otp;
import com.icastar.platform.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OtpService {

    private final OtpRepository otpRepository;
    private final EmailService emailService;
    private final SmsService smsService;

    @Value("${icastar.otp.expiration-minutes}")
    private int otpExpirationMinutes;

    @Value("${icastar.otp.length}")
    private int otpLength;

    public String generateOtp() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < otpLength; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    public void sendOtp(String mobile, String email, Otp.OtpType otpType) {
        // Invalidate existing OTPs for this mobile/email
        invalidateExistingOtps(mobile, email);

        // Generate new OTP
        String otpCode = generateOtp();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(otpExpirationMinutes);

        // Save OTP to database
        Otp otp = new Otp();
        otp.setMobile(mobile);
        otp.setEmail(email);
        otp.setOtpCode(otpCode);
        otp.setOtpType(otpType);
        otp.setExpiresAt(expiresAt);
        otp.setStatus(Otp.OtpStatus.PENDING);
        otp.setAttempts(0);
        otp.setMaxAttempts(3);

        otpRepository.save(otp);

        // Send OTP via SMS
        if (mobile != null && !mobile.isEmpty()) {
            smsService.sendOtpSms(mobile, otpCode);
        }

        // Send OTP via Email (if email provided)
        if (email != null && !email.isEmpty()) {
            emailService.sendOtpEmail(email, otpCode);
        }

        log.info("OTP sent successfully to mobile: {} and email: {}", mobile, email);
    }

    public boolean verifyOtp(String mobile, String email, String otpCode, Otp.OtpType otpType) {
        Otp otp = otpRepository.findByMobileAndEmailAndOtpTypeAndStatus(
                mobile, email, otpType, Otp.OtpStatus.PENDING)
                .orElse(null);

        if (otp == null) {
            log.warn("No pending OTP found for mobile: {} and email: {}", mobile, email);
            return false;
        }

        // Check if OTP has expired
        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            otp.setStatus(Otp.OtpStatus.EXPIRED);
            otpRepository.save(otp);
            log.warn("OTP expired for mobile: {} and email: {}", mobile, email);
            return false;
        }

        // Check if max attempts exceeded
        if (otp.getAttempts() >= otp.getMaxAttempts()) {
            otp.setStatus(Otp.OtpStatus.FAILED);
            otpRepository.save(otp);
            log.warn("Max OTP attempts exceeded for mobile: {} and email: {}", mobile, email);
            return false;
        }

        // Increment attempts
        otp.setAttempts(otp.getAttempts() + 1);

        // Verify OTP
        if (otp.getOtpCode().equals(otpCode)) {
            otp.setStatus(Otp.OtpStatus.VERIFIED);
            otp.setVerifiedAt(LocalDateTime.now());
            otpRepository.save(otp);
            log.info("OTP verified successfully for mobile: {} and email: {}", mobile, email);
            return true;
        } else {
            otpRepository.save(otp);
            log.warn("Invalid OTP for mobile: {} and email: {}", mobile, email);
            return false;
        }
    }

    private void invalidateExistingOtps(String mobile, String email) {
        otpRepository.findByMobileAndEmailAndStatus(mobile, email, Otp.OtpStatus.PENDING)
                .forEach(otp -> {
                    otp.setStatus(Otp.OtpStatus.EXPIRED);
                    otpRepository.save(otp);
                });
    }

    public void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        otpRepository.findByExpiresAtBeforeAndStatus(now, Otp.OtpStatus.PENDING)
                .forEach(otp -> {
                    otp.setStatus(Otp.OtpStatus.EXPIRED);
                    otpRepository.save(otp);
                });
    }
}
