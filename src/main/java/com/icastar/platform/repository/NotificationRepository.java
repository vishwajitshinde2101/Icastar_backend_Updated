package com.icastar.platform.repository;

import com.icastar.platform.entity.Notification;
import com.icastar.platform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /**
     * Find all notifications for a user with pagination
     */
    Page<Notification> findByUserOrderBySentAtDesc(User user, Pageable pageable);

    /**
     * Find unread notifications for a user
     */
    List<Notification> findByUserAndIsReadFalseOrderBySentAtDesc(User user);

    /**
     * Count unread notifications for a user
     */
    Long countByUserAndIsReadFalse(User user);

    /**
     * Find notifications by user and read status
     */
    Page<Notification> findByUserAndIsReadOrderBySentAtDesc(User user, Boolean isRead, Pageable pageable);

    /**
     * Mark all notifications as read for a user
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = CURRENT_TIMESTAMP WHERE n.user = :user AND n.isRead = false")
    int markAllAsReadByUser(@Param("user") User user);

    /**
     * Delete old read notifications
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.user = :user AND n.isRead = true AND n.sentAt < :beforeDate")
    int deleteOldReadNotifications(@Param("user") User user, @Param("beforeDate") java.time.LocalDateTime beforeDate);

    /**
     * Check if user has any unread notifications
     */
    boolean existsByUserAndIsReadFalse(User user);
}
