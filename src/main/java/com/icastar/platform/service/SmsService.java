package com.icastar.platform.service;

import com.icastar.platform.entity.CommunicationLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    private final CommunicationLogService communicationLogService;

    // TODO: Integrate with actual SMS provider (Twilio, AWS SNS, etc.)
    // This is a placeholder implementation

    @Transactional
    public void sendOtpSms(String mobile, String otp) {
        CommunicationLog logEntry = null;
        try {
            // Create communication log
            String messageText = "Your iCastar OTP is: " + otp + ". Valid for 5 minutes.";
            
            logEntry = communicationLogService.createLog(
                CommunicationLog.CommunicationType.SMS,
                null,
                mobile,
                "iCastar OTP Verification",
                messageText,
                "OTP_VERIFICATION_SMS",
                null,
                "{\"otp\":\"" + otp + "\",\"type\":\"verification\"}"
            );

            // Placeholder for SMS integration
            // In production, integrate with SMS provider like:
            // - Twilio
            // - AWS SNS
            // - TextLocal
            // - MSG91
            
            // Example Twilio integration (commented out):
            /*
            Twilio.init(accountSid, authToken);
            Message message = Message.creator(
                new PhoneNumber(mobile),
                new PhoneNumber(twilioPhoneNumber),
                messageText
            ).create();
            */
            
            // Mark as sent (placeholder - in production, use actual SMS provider response)
            communicationLogService.markAsSent(logEntry.getId(), null);
            log.info("SMS OTP sent to {}: {}", mobile, otp);
            
        } catch (Exception e) {
            log.error("Failed to send SMS OTP to: {}", mobile, e);
            if (logEntry != null) {
                communicationLogService.markAsFailed(logEntry.getId(), e.getMessage());
            }
            // In production, you might want to throw an exception or handle this differently
        }
    }

    @Transactional
    public void sendWelcomeSms(String mobile, String firstName) {
        CommunicationLog logEntry = null;
        try {
            // Create communication log
            String messageText = "Welcome to iCastar, " + firstName + "! Your account is ready.";
            
            logEntry = communicationLogService.createLog(
                CommunicationLog.CommunicationType.SMS,
                null,
                mobile,
                "Welcome to iCastar",
                messageText,
                "WELCOME_SMS",
                null,
                "{\"firstName\":\"" + firstName + "\",\"type\":\"welcome\"}"
            );

            // Placeholder for SMS integration
            // Mark as sent (placeholder - in production, use actual SMS provider response)
            communicationLogService.markAsSent(logEntry.getId(), null);
            log.info("Welcome SMS sent to {}: Welcome to iCastar, {}!", mobile, firstName);
            
        } catch (Exception e) {
            log.error("Failed to send welcome SMS to: {}", mobile, e);
            if (logEntry != null) {
                communicationLogService.markAsFailed(logEntry.getId(), e.getMessage());
            }
        }
    }

    @Transactional
    public void sendJobAlertSms(String mobile, String artistName, String jobTitle) {
        CommunicationLog logEntry = null;
        try {
            // Create communication log
            String messageText = "New job '" + jobTitle + "' matches your profile. Check your iCastar account!";
            
            logEntry = communicationLogService.createLog(
                CommunicationLog.CommunicationType.SMS,
                null,
                mobile,
                "Job Alert",
                messageText,
                "JOB_ALERT_SMS",
                null,
                "{\"artistName\":\"" + artistName + "\",\"jobTitle\":\"" + jobTitle + "\",\"type\":\"job_alert\"}"
            );

            // Placeholder for SMS integration
            // Mark as sent (placeholder - in production, use actual SMS provider response)
            communicationLogService.markAsSent(logEntry.getId(), null);
            log.info("Job alert SMS sent to {}: New job '{}' matches your profile", mobile, jobTitle);
            
        } catch (Exception e) {
            log.error("Failed to send job alert SMS to: {}", mobile, e);
            if (logEntry != null) {
                communicationLogService.markAsFailed(logEntry.getId(), e.getMessage());
            }
        }
    }
}
