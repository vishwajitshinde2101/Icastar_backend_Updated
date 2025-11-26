package com.icastar.platform.service;

import com.icastar.platform.entity.CommunicationLog;
import com.icastar.platform.entity.User;
import com.icastar.platform.repository.CommunicationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunicationLogService {

    private final CommunicationLogRepository communicationLogRepository;

    /**
     * Create a new communication log entry
     */
    @Transactional
    public CommunicationLog createLog(CommunicationLog.CommunicationType type,
                                    String recipientEmail,
                                    String recipientMobile,
                                    String subject,
                                    String message,
                                    String templateName,
                                    User user,
                                    String metadata) {
        
        CommunicationLog log = new CommunicationLog();
        log.setCommunicationType(type);
        log.setStatus(CommunicationLog.CommunicationStatus.PENDING);
        log.setRecipientEmail(recipientEmail);
        log.setRecipientMobile(recipientMobile);
        log.setSubject(subject);
        log.setMessage(message);
        log.setTemplateName(templateName);
        log.setUser(user);
        log.setMetadata(metadata);
        log.setExternalId(UUID.randomUUID().toString());
        
        return communicationLogRepository.save(log);
    }

    /**
     * Mark communication as sent
     */
    @Transactional
    public void markAsSent(Long logId, String externalId) {
        Optional<CommunicationLog> logOpt = communicationLogRepository.findById(logId);
        if (logOpt.isPresent()) {
            CommunicationLog communicationLog = logOpt.get();
            communicationLog.markAsSent();
            if (externalId != null) {
                communicationLog.setExternalId(externalId);
            }
            communicationLogRepository.save(communicationLog);
            log.info("Communication log {} marked as sent", logId);
        }
    }

    /**
     * Mark communication as delivered
     */
    @Transactional
    public void markAsDelivered(Long logId) {
        Optional<CommunicationLog> logOpt = communicationLogRepository.findById(logId);
        if (logOpt.isPresent()) {
            CommunicationLog communicationLog = logOpt.get();
            communicationLog.markAsDelivered();
            communicationLogRepository.save(communicationLog);
            log.info("Communication log {} marked as delivered", logId);
        }
    }

    /**
     * Mark communication as failed
     */
    @Transactional
    public void markAsFailed(Long logId, String errorMessage) {
        Optional<CommunicationLog> logOpt = communicationLogRepository.findById(logId);
        if (logOpt.isPresent()) {
            CommunicationLog communicationLog = logOpt.get();
            communicationLog.markAsFailed(errorMessage);
            communicationLogRepository.save(communicationLog);
            log.error("Communication log {} marked as failed: {}", logId, errorMessage);
        }
    }

    /**
     * Get communication logs by user
     */
    public List<CommunicationLog> getLogsByUser(User user) {
        return communicationLogRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Get communication logs by user with pagination
     */
    public Page<CommunicationLog> getLogsByUser(User user, Pageable pageable) {
        return communicationLogRepository.findByUserOrderByCreatedAtDesc(user, pageable);
    }

    /**
     * Get communication logs by type
     */
    public List<CommunicationLog> getLogsByType(CommunicationLog.CommunicationType type) {
        return communicationLogRepository.findByCommunicationTypeOrderByCreatedAtDesc(type);
    }

    /**
     * Get communication logs by status
     */
    public List<CommunicationLog> getLogsByStatus(CommunicationLog.CommunicationStatus status) {
        return communicationLogRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Get failed communications that can be retried
     */
    public List<CommunicationLog> getRetryableCommunications() {
        return communicationLogRepository.findRetryableCommunications(LocalDateTime.now());
    }

    /**
     * Retry a failed communication
     */
    @Transactional
    public void retryCommunication(Long logId) {
        Optional<CommunicationLog> logOpt = communicationLogRepository.findById(logId);
        if (logOpt.isPresent()) {
            CommunicationLog communicationLog = logOpt.get();
            if (communicationLog.isRetryable()) {
                communicationLog.incrementRetryCount();
                communicationLog.setStatus(CommunicationLog.CommunicationStatus.PENDING);
                communicationLogRepository.save(communicationLog);
                log.info("Communication log {} scheduled for retry", logId);
            }
        }
    }

    /**
     * Get communication statistics
     */
    public Object[] getCommunicationStats(LocalDateTime since) {
        return communicationLogRepository.getCommunicationStats(since);
    }

    /**
     * Get logs by date range
     */
    public List<CommunicationLog> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return communicationLogRepository.findByDateRange(startDate, endDate);
    }

    /**
     * Get logs by date range with pagination
     */
    public Page<CommunicationLog> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return communicationLogRepository.findByDateRange(startDate, endDate, pageable);
    }

    /**
     * Get logs by recipient email
     */
    public List<CommunicationLog> getLogsByEmail(String email) {
        return communicationLogRepository.findByRecipientEmailOrderByCreatedAtDesc(email);
    }

    /**
     * Get logs by recipient mobile
     */
    public List<CommunicationLog> getLogsByMobile(String mobile) {
        return communicationLogRepository.findByRecipientMobileOrderByCreatedAtDesc(mobile);
    }

    /**
     * Get recent communications for a user
     */
    public List<CommunicationLog> getRecentCommunications(User user, LocalDateTime since) {
        return communicationLogRepository.findRecentByUser(user, since);
    }

    /**
     * Get communication log by ID
     */
    public Optional<CommunicationLog> getLogById(Long id) {
        return communicationLogRepository.findById(id);
    }

    /**
     * Get communication log by external ID
     */
    public Optional<CommunicationLog> getLogByExternalId(String externalId) {
        return communicationLogRepository.findByExternalId(externalId);
    }

    /**
     * Delete old communication logs (for cleanup)
     */
    @Transactional
    public void deleteOldLogs(LocalDateTime beforeDate) {
        List<CommunicationLog> oldLogs = communicationLogRepository.findByDateRange(
            LocalDateTime.of(2020, 1, 1, 0, 0), beforeDate);
        communicationLogRepository.deleteAll(oldLogs);
        log.info("Deleted {} old communication logs", oldLogs.size());
    }
}
