package com.icastar.platform.repository;

import com.icastar.platform.entity.Notification;
import com.icastar.platform.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.isRead = false ORDER BY n.sentAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);

    /**
     * Count unread notifications for a user
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.isRead = false")
    Long countUnreadByUserId(@Param("userId") Long userId);

    /**
     * Find notifications by user and type
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId AND n.type = :type ORDER BY n.sentAt DESC")
    List<Notification> findByUserIdAndType(@Param("userId") Long userId, @Param("type") Notification.NotificationType type);

    /**
     * Find all notifications for a user (without pagination)
     */
    List<Notification> findByUserIdOrderBySentAtDesc(Long userId);

    // Artist dashboard queries

    /**
     * Count job invitations (JOB_ALERT notifications)
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user.id = :userId AND n.type = 'JOB_ALERT'")
    Long countJobInvitationsByUserId(@Param("userId") Long userId);

    /**
     * Find recent notifications for artist with pagination
     */
    @Query("SELECT n FROM Notification n WHERE n.user.id = :userId ORDER BY n.sentAt DESC")
    Page<Notification> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);
}
