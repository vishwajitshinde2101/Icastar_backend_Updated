package com.icastar.platform.service;

import com.icastar.platform.entity.Notification;
import com.icastar.platform.entity.User;
import com.icastar.platform.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * Get all notifications for a user with pagination
     */
    public Page<Notification> getNotificationsByUser(User user, Pageable pageable) {
        return notificationRepository.findByUserOrderBySentAtDesc(user, pageable);
    }

    /**
     * Get unread notifications for a user
     */
    public List<Notification> getUnreadNotifications(User user) {
        return notificationRepository.findByUserAndIsReadFalseOrderBySentAtDesc(user);
    }

    /**
     * Get unread notification count for a user
     */
    public Long getUnreadCount(User user) {
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    /**
     * Check if user has unread notifications
     */
    public boolean hasUnreadNotifications(User user) {
        return notificationRepository.existsByUserAndIsReadFalse(user);
    }

    /**
     * Get a notification by ID
     */
    public Optional<Notification> getNotificationById(Long id) {
        return notificationRepository.findById(id);
    }

    /**
     * Mark a notification as read
     */
    @Transactional
    public void markAsRead(Long notificationId, User user) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            // Verify the notification belongs to the user
            if (notification.getUser().getId().equals(user.getId())) {
                notification.setIsRead(true);
                notification.setReadAt(LocalDateTime.now());
                notificationRepository.save(notification);
                log.info("Notification {} marked as read for user {}", notificationId, user.getEmail());
            } else {
                throw new RuntimeException("Unauthorized access to notification");
            }
        } else {
            throw new RuntimeException("Notification not found");
        }
    }

    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public int markAllAsRead(User user) {
        int count = notificationRepository.markAllAsReadByUser(user);
        log.info("Marked {} notifications as read for user {}", count, user.getEmail());
        return count;
    }

    /**
     * Delete a notification
     */
    @Transactional
    public void deleteNotification(Long notificationId, User user) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isPresent()) {
            Notification notification = notificationOpt.get();
            // Verify the notification belongs to the user
            if (notification.getUser().getId().equals(user.getId())) {
                notificationRepository.delete(notification);
                log.info("Notification {} deleted for user {}", notificationId, user.getEmail());
            } else {
                throw new RuntimeException("Unauthorized access to notification");
            }
        } else {
            throw new RuntimeException("Notification not found");
        }
    }

    /**
     * Create a notification
     */
    @Transactional
    public Notification createNotification(User user, String title, String message,
                                          Notification.NotificationType type,
                                          Notification.Priority priority,
                                          String actionUrl, String metadata) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setPriority(priority != null ? priority : Notification.Priority.MEDIUM);
        notification.setIsRead(false);
        notification.setSentAt(LocalDateTime.now());
        notification.setActionUrl(actionUrl);
        notification.setMetadata(metadata);
        notification.setEmailSent(false);
        notification.setPushSent(false);

        notification = notificationRepository.save(notification);
        log.info("Created notification {} for user {}", notification.getId(), user.getEmail());
        return notification;
    }

    /**
     * Clean up old read notifications (older than specified days)
     */
    @Transactional
    public int cleanupOldNotifications(User user, int daysOld) {
        LocalDateTime beforeDate = LocalDateTime.now().minusDays(daysOld);
        int count = notificationRepository.deleteOldReadNotifications(user, beforeDate);
        log.info("Cleaned up {} old notifications for user {}", count, user.getEmail());
        return count;
    }
}
