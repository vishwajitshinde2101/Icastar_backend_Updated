package com.icastar.platform.service;

import com.icastar.platform.entity.CommunicationLog;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public void sendInterviewScheduleEmail(String toEmail, String applicantName, String jobTitle,
                                           String companyName, String recruiterName,
                                           LocalDateTime interviewDateTime, String interviewType,
                                           String interviewLocation, String meetingLink, String notes) {
        CommunicationLog logEntry = null;
        try {
            // Format interview date and time
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            String formattedDate = interviewDateTime.format(dateFormatter);
            String formattedTime = interviewDateTime.format(timeFormatter);

            // Build HTML email
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<!DOCTYPE html>");
            emailBody.append("<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>");
            emailBody.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>");

            // Header
            emailBody.append("<div style='background-color: #4CAF50; color: white; padding: 30px; text-align: center; border-radius: 5px 5px 0 0;'>");
            emailBody.append("<h1 style='margin: 0; font-size: 28px;'>Interview Scheduled</h1>");
            emailBody.append("</div>");

            // Content
            emailBody.append("<div style='background-color: #f9f9f9; padding: 30px; border-radius: 0 0 5px 5px;'>");
            emailBody.append("<p style='font-size: 16px;'>Dear ").append(applicantName).append(",</p>");
            emailBody.append("<p style='font-size: 16px;'>Congratulations! Your application for the position of <strong>").append(jobTitle).append("</strong> at <strong>").append(companyName).append("</strong> has been shortlisted.</p>");
            emailBody.append("<p style='font-size: 16px;'>We would like to invite you for an interview with the following details:</p>");

            // Interview Details Box
            emailBody.append("<div style='background-color: white; padding: 20px; border-radius: 5px; margin: 20px 0; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>");
            emailBody.append("<div style='margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px solid #e0e0e0;'>");
            emailBody.append("<div style='color: #666; font-size: 14px;'>Date</div>");
            emailBody.append("<div style='font-size: 16px; font-weight: bold; color: #333;'>").append(formattedDate).append("</div>");
            emailBody.append("</div>");

            emailBody.append("<div style='margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px solid #e0e0e0;'>");
            emailBody.append("<div style='color: #666; font-size: 14px;'>Time</div>");
            emailBody.append("<div style='font-size: 16px; font-weight: bold; color: #333;'>").append(formattedTime).append("</div>");
            emailBody.append("</div>");

            emailBody.append("<div style='margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px solid #e0e0e0;'>");
            emailBody.append("<div style='color: #666; font-size: 14px;'>Interview Type</div>");
            emailBody.append("<div style='font-size: 16px; font-weight: bold; color: #333;'>").append(interviewType).append("</div>");
            emailBody.append("</div>");

            if (interviewLocation != null && !interviewLocation.isEmpty()) {
                emailBody.append("<div style='margin-bottom: 15px; padding-bottom: 15px; border-bottom: 1px solid #e0e0e0;'>");
                emailBody.append("<div style='color: #666; font-size: 14px;'>Location</div>");
                emailBody.append("<div style='font-size: 16px; font-weight: bold; color: #333;'>").append(interviewLocation).append("</div>");
                emailBody.append("</div>");
            }

            emailBody.append("<div style='margin-bottom: 15px;'>");
            emailBody.append("<div style='color: #666; font-size: 14px;'>Interviewer</div>");
            emailBody.append("<div style='font-size: 16px; font-weight: bold; color: #333;'>").append(recruiterName).append("</div>");
            emailBody.append("</div>");
            emailBody.append("</div>");

            // Meeting Link Button
            if (meetingLink != null && !meetingLink.isEmpty()) {
                emailBody.append("<div style='text-align: center; margin: 30px 0;'>");
                emailBody.append("<a href='").append(meetingLink).append("' style='display: inline-block; background-color: #4CAF50; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; font-size: 16px; font-weight: bold;'>Join Interview</a>");
                emailBody.append("</div>");
            }

            // Additional Notes
            if (notes != null && !notes.isEmpty()) {
                emailBody.append("<div style='background-color: #fff3cd; border-left: 4px solid #ffc107; padding: 15px; margin: 20px 0; border-radius: 3px;'>");
                emailBody.append("<div style='font-weight: bold; margin-bottom: 5px;'>Additional Notes:</div>");
                emailBody.append("<div>").append(notes).append("</div>");
                emailBody.append("</div>");
            }

            // Instructions
            emailBody.append("<div style='background-color: white; padding: 20px; border-radius: 5px; margin: 20px 0;'>");
            emailBody.append("<h3 style='color: #4CAF50; margin-top: 0;'>What to Bring:</h3>");
            emailBody.append("<ul style='padding-left: 20px;'>");
            emailBody.append("<li>Updated resume/portfolio</li>");
            emailBody.append("<li>Valid ID proof</li>");
            emailBody.append("<li>Any relevant certificates or documents</li>");
            emailBody.append("<li>Questions you may have about the role</li>");
            emailBody.append("</ul>");
            emailBody.append("</div>");

            emailBody.append("<p style='font-size: 16px;'>Please confirm your attendance by replying to this email.</p>");
            emailBody.append("<p style='font-size: 16px;'>If you have any questions or need to reschedule, please contact us immediately.</p>");
            emailBody.append("<p style='font-size: 16px;'>We look forward to meeting you!</p>");

            // Footer
            emailBody.append("<div style='margin-top: 30px; padding-top: 20px; border-top: 2px solid #e0e0e0; text-align: center; color: #666;'>");
            emailBody.append("<p style='margin: 5px 0;'>Best regards,</p>");
            emailBody.append("<p style='margin: 5px 0; font-weight: bold;'>").append(companyName).append("</p>");
            emailBody.append("<p style='margin: 5px 0; font-size: 12px;'>Powered by iCastar</p>");
            emailBody.append("</div>");

            emailBody.append("</div></div></body></html>");

            // Create communication log
            String metadata = String.format("{\"applicantName\":\"%s\",\"jobTitle\":\"%s\",\"companyName\":\"%s\",\"interviewDateTime\":\"%s\",\"interviewType\":\"%s\",\"type\":\"interview_schedule\"}",
                    applicantName, jobTitle, companyName, interviewDateTime.toString(), interviewType);

            logEntry = communicationLogService.createLog(
                CommunicationLog.CommunicationType.EMAIL,
                toEmail,
                null,
                "Interview Scheduled - " + jobTitle,
                emailBody.toString(),
                "INTERVIEW_SCHEDULE",
                null,
                metadata
            );

            // Send HTML email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Interview Scheduled - " + jobTitle);
            helper.setText(emailBody.toString(), true); // true = HTML

            mailSender.send(mimeMessage);

            // Mark as sent
            communicationLogService.markAsSent(logEntry.getId(), null);
            log.info("Interview schedule email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send interview schedule email to: {}", toEmail, e);
            if (logEntry != null) {
                communicationLogService.markAsFailed(logEntry.getId(), e.getMessage());
            }
            // Don't throw exception - we don't want to prevent interview scheduling if email fails
            log.warn("Interview will be scheduled despite email failure for: {}", toEmail);
        }
    }

    /**
     * Send email notification when applicant is hired
     */
    @Transactional
    public void sendHiredEmail(String toEmail, String applicantName, String jobTitle,
                                String companyName, String notes) {
        CommunicationLog logEntry = null;
        try {
            // Build HTML email
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<!DOCTYPE html>");
            emailBody.append("<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>");
            emailBody.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>");

            // Header
            emailBody.append("<div style='background-color: #4CAF50; color: white; padding: 30px; text-align: center; border-radius: 5px 5px 0 0;'>");
            emailBody.append("<h1 style='margin: 0; font-size: 28px;'>Congratulations!</h1>");
            emailBody.append("<p style='margin: 10px 0 0 0; font-size: 18px;'>You've Been Selected!</p>");
            emailBody.append("</div>");

            // Content
            emailBody.append("<div style='background-color: #f9f9f9; padding: 30px; border-radius: 0 0 5px 5px;'>");
            emailBody.append("<p style='font-size: 16px;'>Dear ").append(applicantName).append(",</p>");
            emailBody.append("<p style='font-size: 16px;'>We are thrilled to inform you that you have been <strong style='color: #4CAF50;'>selected</strong> for the position of <strong>").append(jobTitle).append("</strong> at <strong>").append(companyName).append("</strong>!</p>");

            emailBody.append("<div style='background-color: white; padding: 20px; border-radius: 5px; margin: 20px 0; text-align: center; box-shadow: 0 2px 4px rgba(0,0,0,0.1);'>");
            emailBody.append("<div style='font-size: 48px; margin-bottom: 10px;'>🎉</div>");
            emailBody.append("<div style='font-size: 20px; font-weight: bold; color: #4CAF50;'>Welcome to the Team!</div>");
            emailBody.append("</div>");

            // Notes section
            if (notes != null && !notes.isEmpty()) {
                emailBody.append("<div style='background-color: #e8f5e9; border-left: 4px solid #4CAF50; padding: 15px; margin: 20px 0; border-radius: 3px;'>");
                emailBody.append("<div style='font-weight: bold; margin-bottom: 5px;'>Message from the Recruiter:</div>");
                emailBody.append("<div>").append(notes).append("</div>");
                emailBody.append("</div>");
            }

            emailBody.append("<p style='font-size: 16px;'>Our team will reach out to you shortly with further details about the next steps, including onboarding information and documentation requirements.</p>");
            emailBody.append("<p style='font-size: 16px;'>If you have any questions, please don't hesitate to reach out.</p>");

            // Footer
            emailBody.append("<div style='margin-top: 30px; padding-top: 20px; border-top: 2px solid #e0e0e0; text-align: center; color: #666;'>");
            emailBody.append("<p style='margin: 5px 0;'>Best regards,</p>");
            emailBody.append("<p style='margin: 5px 0; font-weight: bold;'>").append(companyName).append("</p>");
            emailBody.append("<p style='margin: 5px 0; font-size: 12px;'>Powered by iCastar</p>");
            emailBody.append("</div>");

            emailBody.append("</div></div></body></html>");

            // Create communication log
            String metadata = String.format("{\"applicantName\":\"%s\",\"jobTitle\":\"%s\",\"companyName\":\"%s\",\"result\":\"HIRED\",\"type\":\"interview_result\"}",
                    applicantName, jobTitle, companyName);

            logEntry = communicationLogService.createLog(
                CommunicationLog.CommunicationType.EMAIL,
                toEmail,
                null,
                "Congratulations! You've Been Selected - " + jobTitle,
                emailBody.toString(),
                "HIRED_NOTIFICATION",
                null,
                metadata
            );

            // Send HTML email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Congratulations! You've Been Selected - " + jobTitle);
            helper.setText(emailBody.toString(), true);

            mailSender.send(mimeMessage);

            // Mark as sent
            communicationLogService.markAsSent(logEntry.getId(), null);
            log.info("Hired notification email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send hired notification email to: {}", toEmail, e);
            if (logEntry != null) {
                communicationLogService.markAsFailed(logEntry.getId(), e.getMessage());
            }
        }
    }

    /**
     * Send email notification when applicant is rejected
     */
    @Transactional
    public void sendRejectionEmail(String toEmail, String applicantName, String jobTitle,
                                    String companyName, String notes) {
        CommunicationLog logEntry = null;
        try {
            // Build HTML email
            StringBuilder emailBody = new StringBuilder();
            emailBody.append("<!DOCTYPE html>");
            emailBody.append("<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>");
            emailBody.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px;'>");

            // Header
            emailBody.append("<div style='background-color: #607D8B; color: white; padding: 30px; text-align: center; border-radius: 5px 5px 0 0;'>");
            emailBody.append("<h1 style='margin: 0; font-size: 28px;'>Application Update</h1>");
            emailBody.append("</div>");

            // Content
            emailBody.append("<div style='background-color: #f9f9f9; padding: 30px; border-radius: 0 0 5px 5px;'>");
            emailBody.append("<p style='font-size: 16px;'>Dear ").append(applicantName).append(",</p>");
            emailBody.append("<p style='font-size: 16px;'>Thank you for taking the time to interview for the position of <strong>").append(jobTitle).append("</strong> at <strong>").append(companyName).append("</strong>.</p>");
            emailBody.append("<p style='font-size: 16px;'>After careful consideration, we regret to inform you that we have decided to move forward with other candidates whose qualifications more closely match our current requirements.</p>");

            // Notes/Feedback section
            if (notes != null && !notes.isEmpty()) {
                emailBody.append("<div style='background-color: #fff3e0; border-left: 4px solid #ff9800; padding: 15px; margin: 20px 0; border-radius: 3px;'>");
                emailBody.append("<div style='font-weight: bold; margin-bottom: 5px;'>Feedback:</div>");
                emailBody.append("<div>").append(notes).append("</div>");
                emailBody.append("</div>");
            }

            emailBody.append("<p style='font-size: 16px;'>We encourage you to continue exploring opportunities on iCastar and apply for other positions that match your skills and experience.</p>");
            emailBody.append("<p style='font-size: 16px;'>We appreciate your interest in ").append(companyName).append(" and wish you the best in your career endeavors.</p>");

            // Footer
            emailBody.append("<div style='margin-top: 30px; padding-top: 20px; border-top: 2px solid #e0e0e0; text-align: center; color: #666;'>");
            emailBody.append("<p style='margin: 5px 0;'>Best regards,</p>");
            emailBody.append("<p style='margin: 5px 0; font-weight: bold;'>").append(companyName).append("</p>");
            emailBody.append("<p style='margin: 5px 0; font-size: 12px;'>Powered by iCastar</p>");
            emailBody.append("</div>");

            emailBody.append("</div></div></body></html>");

            // Create communication log
            String metadata = String.format("{\"applicantName\":\"%s\",\"jobTitle\":\"%s\",\"companyName\":\"%s\",\"result\":\"REJECTED\",\"type\":\"interview_result\"}",
                    applicantName, jobTitle, companyName);

            logEntry = communicationLogService.createLog(
                CommunicationLog.CommunicationType.EMAIL,
                toEmail,
                null,
                "Application Update - " + jobTitle,
                emailBody.toString(),
                "REJECTION_NOTIFICATION",
                null,
                metadata
            );

            // Send HTML email
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("Application Update - " + jobTitle);
            helper.setText(emailBody.toString(), true);

            mailSender.send(mimeMessage);

            // Mark as sent
            communicationLogService.markAsSent(logEntry.getId(), null);
            log.info("Rejection notification email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send rejection notification email to: {}", toEmail, e);
            if (logEntry != null) {
                communicationLogService.markAsFailed(logEntry.getId(), e.getMessage());
            }
        }
    }
}
