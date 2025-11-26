package com.icastar.platform.service;

import com.icastar.platform.entity.CommunicationLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final CommunicationLogService communicationLogService;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Transactional
    public void sendOtpEmail(String toEmail, String otp) {
        CommunicationLog logEntry = null;
        try {
            // Create communication log
            String messageText = "Your OTP for iCastar verification is: " + otp + 
                               "\n\nThis OTP is valid for 5 minutes." +
                               "\n\nIf you didn't request this OTP, please ignore this email." +
                               "\n\nBest regards,\nThe iCastar Team";
            
            logEntry = communicationLogService.createLog(
                CommunicationLog.CommunicationType.EMAIL,
                toEmail,
                null,
                "iCastar - OTP Verification",
                messageText,
                "OTP_VERIFICATION",
                null,
                "{\"otp\":\"" + otp + "\",\"type\":\"verification\"}"
            );

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("iCastar - OTP Verification");
            message.setText(messageText);

            mailSender.send(message);
            
            // Mark as sent
            communicationLogService.markAsSent(logEntry.getId(), null);
            log.info("OTP email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send OTP email to: {}", toEmail, e);
            if (logEntry != null) {
                communicationLogService.markAsFailed(logEntry.getId(), e.getMessage());
            }
            // In production, you might want to throw an exception or handle this differently
        }
    }

    @Transactional
    public void sendWelcomeEmail(String toEmail, String firstName) {
        CommunicationLog logEntry = null;
        try {
            // Create communication log
            String messageText = "Dear " + firstName + ",\n\n" +
                               "Welcome to iCastar - your gateway to the entertainment industry!\n\n" +
                               "Your account has been successfully created. You can now:\n" +
                               "- Create your professional profile\n" +
                               "- Browse and apply for jobs\n" +
                               "- Connect with recruiters\n" +
                               "- Showcase your talent\n\n" +
                               "If you have any questions, feel free to contact our support team.\n\n" +
                               "Best regards,\nThe iCastar Team";
            
            logEntry = communicationLogService.createLog(
                CommunicationLog.CommunicationType.EMAIL,
                toEmail,
                null,
                "Welcome to iCastar!",
                messageText,
                "WELCOME_EMAIL",
                null,
                "{\"firstName\":\"" + firstName + "\",\"type\":\"welcome\"}"
            );

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to iCastar!");
            message.setText(messageText);

            mailSender.send(message);
            
            // Mark as sent
            communicationLogService.markAsSent(logEntry.getId(), null);
            log.info("Welcome email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send welcome email to: {}", toEmail, e);
            if (logEntry != null) {
                communicationLogService.markAsFailed(logEntry.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    public void sendJobAlertEmail(String toEmail, String artistName, String jobTitle, String companyName) {
        CommunicationLog logEntry = null;
        try {
            // Create communication log
            String messageText = "Dear " + artistName + ",\n\n" +
                               "A new job opportunity that matches your profile has been posted:\n\n" +
                               "Job Title: " + jobTitle + "\n" +
                               "Company: " + companyName + "\n\n" +
                               "Log in to your iCastar account to view details and apply.\n\n" +
                               "Best regards,\nThe iCastar Team";
            
            logEntry = communicationLogService.createLog(
                CommunicationLog.CommunicationType.EMAIL,
                toEmail,
                null,
                "New Job Alert - " + jobTitle,
                messageText,
                "JOB_ALERT",
                null,
                "{\"artistName\":\"" + artistName + "\",\"jobTitle\":\"" + jobTitle + "\",\"companyName\":\"" + companyName + "\",\"type\":\"job_alert\"}"
            );

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("New Job Alert - " + jobTitle);
            message.setText(messageText);

            mailSender.send(message);
            
            // Mark as sent
            communicationLogService.markAsSent(logEntry.getId(), null);
            log.info("Job alert email sent successfully to: {}", toEmail);
            
        } catch (Exception e) {
            log.error("Failed to send job alert email to: {}", toEmail, e);
            if (logEntry != null) {
                communicationLogService.markAsFailed(logEntry.getId(), e.getMessage());
            }
        }
    }
}
