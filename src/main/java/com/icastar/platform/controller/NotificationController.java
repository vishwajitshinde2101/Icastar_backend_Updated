package com.icastar.platform.controller;

import com.icastar.platform.entity.Notification;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.NotificationService;
import com.icastar.platform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notifications", description = "APIs for managing user notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    /**
     * Get all notifications with pagination
     * GET /api/notifications
     */
    @Operation(summary = "Get all notifications", description = "Get all notifications for the authenticated user with pagination")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping
    public ResponseEntity<Map<String, Object>> getNotifications(
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sentAt"));
            Page<Notification> notificationsPage = notificationService.getNotificationsByUser(user, pageable);

            // Transform notifications to response format
            List<Map<String, Object>> notifications = notificationsPage.getContent().stream()
                    .map(this::transformNotification)
                    .collect(Collectors.toList());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("notifications", notifications);
            response.put("totalElements", notificationsPage.getTotalElements());
            response.put("totalPages", notificationsPage.getTotalPages());
            response.put("currentPage", notificationsPage.getNumber());
            response.put("hasUnread", notificationService.hasUnreadNotifications(user));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving notifications", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch notifications: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get unread notification count
     * GET /api/notifications/unread-count
     */
    @Operation(summary = "Get unread notification count", description = "Get the count of unread notifications for the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Long count = notificationService.getUnreadCount(user);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("count", count);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrieving unread count", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch unread count: " + e.getMessage());
            response.put("count", 0);
            return ResponseEntity.ok(response);
        }
    }

    /**
     * Mark a specific notification as read
     * PUT /api/notifications/{notificationId}/read
     */
    @Operation(summary = "Mark notification as read", description = "Mark a specific notification as read")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(@PathVariable Long notificationId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            notificationService.markAsRead(notificationId, user);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("message", "Notification marked as read");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error marking notification as read", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to mark notification as read: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Mark all notifications as read
     * PUT /api/notifications/mark-all-read
     */
    @Operation(summary = "Mark all notifications as read", description = "Mark all notifications as read for the authenticated user")
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/mark-all-read")
    public ResponseEntity<Map<String, Object>> markAllAsRead() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            int count = notificationService.markAllAsRead(user);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("message", count + " notifications marked as read");
            response.put("count", count);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error marking all notifications as read", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to mark all notifications as read: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Delete a notification
     * DELETE /api/notifications/{notificationId}
     */
    @Operation(summary = "Delete notification", description = "Delete a specific notification")
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, Object>> deleteNotification(@PathVariable Long notificationId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();

            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            notificationService.deleteNotification(notificationId, user);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", true);
            response.put("message", "Notification deleted successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting notification", e);
            Map<String, Object> response = new LinkedHashMap<>();
            response.put("success", false);
            response.put("message", "Failed to delete notification: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Helper method to transform Notification entity to response format
     */
    private Map<String, Object> transformNotification(Notification notification) {
        Map<String, Object> notificationMap = new LinkedHashMap<>();
        notificationMap.put("id", notification.getId());
        notificationMap.put("userId", notification.getUser().getId());
        notificationMap.put("type", notification.getType().toString());
        notificationMap.put("title", notification.getTitle());
        notificationMap.put("message", notification.getMessage());
        notificationMap.put("actionUrl", notification.getActionUrl());
        notificationMap.put("metadata", notification.getMetadata());
        notificationMap.put("isRead", notification.getIsRead());
        notificationMap.put("createdAt", notification.getSentAt() != null ? notification.getSentAt().toString() : null);
        return notificationMap;
    }
}
