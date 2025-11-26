package com.icastar.platform.controller;

import com.icastar.platform.entity.CommunicationLog;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.CommunicationLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/communications")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("hasRole('ADMIN')")
public class AdminCommunicationController {

    private final CommunicationLogService communicationLogService;

    /**
     * Get all communication logs with pagination
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllLogs(Pageable pageable) {
        try {
            Page<CommunicationLog> logs = communicationLogService.getLogsByDateRange(
                LocalDateTime.now().minusMonths(1), 
                LocalDateTime.now(), 
                pageable
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", logs.getContent());
            response.put("totalElements", logs.getTotalElements());
            response.put("totalPages", logs.getTotalPages());
            response.put("currentPage", logs.getNumber());
            response.put("size", logs.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching communication logs", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch communication logs");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get communication logs by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<Map<String, Object>> getLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        try {
            Page<CommunicationLog> logs = communicationLogService.getLogsByDateRange(startDate, endDate, pageable);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", logs.getContent());
            response.put("totalElements", logs.getTotalElements());
            response.put("totalPages", logs.getTotalPages());
            response.put("currentPage", logs.getNumber());
            response.put("size", logs.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching communication logs by date range", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch communication logs");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get communication logs by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<Map<String, Object>> getLogsByType(
            @PathVariable CommunicationLog.CommunicationType type,
            Pageable pageable) {
        try {
            List<CommunicationLog> allLogs = communicationLogService.getLogsByType(type);
            
            // Convert to page manually since repository doesn't return Page
            int start = Math.max(0, (int) pageable.getOffset());
            int end = Math.min((start + pageable.getPageSize()), allLogs.size());
            
            // Additional safety check to prevent IndexOutOfBoundsException
            if (start >= allLogs.size()) {
                start = 0;
                end = Math.min(pageable.getPageSize(), allLogs.size());
            }
            
            List<CommunicationLog> pageContent = allLogs.subList(start, end);
            Page<CommunicationLog> page = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, allLogs.size());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", page.getContent());
            response.put("totalElements", page.getTotalElements());
            response.put("totalPages", page.getTotalPages());
            response.put("currentPage", page.getNumber());
            response.put("size", page.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching communication logs by type", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch communication logs");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get communication logs by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<Map<String, Object>> getLogsByStatus(
            @PathVariable CommunicationLog.CommunicationStatus status,
            Pageable pageable) {
        try {
            List<CommunicationLog> logs = communicationLogService.getLogsByStatus(status);
            
            // Convert to page manually
            int start = Math.max(0, (int) pageable.getOffset());
            int end = Math.min((start + pageable.getPageSize()), logs.size());
            
            // Additional safety check to prevent IndexOutOfBoundsException
            if (start >= logs.size()) {
                start = 0;
                end = Math.min(pageable.getPageSize(), logs.size());
            }
            
            List<CommunicationLog> pageContent = logs.subList(start, end);
            Page<CommunicationLog> page = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, logs.size());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", page.getContent());
            response.put("totalElements", page.getTotalElements());
            response.put("totalPages", page.getTotalPages());
            response.put("currentPage", page.getNumber());
            response.put("size", page.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching communication logs by status", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch communication logs");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get communication logs by recipient email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<Map<String, Object>> getLogsByEmail(
            @PathVariable String email,
            Pageable pageable) {
        try {
            List<CommunicationLog> logs = communicationLogService.getLogsByEmail(email);
            
            // Convert to page manually
            int start = Math.max(0, (int) pageable.getOffset());
            int end = Math.min((start + pageable.getPageSize()), logs.size());
            
            // Additional safety check to prevent IndexOutOfBoundsException
            if (start >= logs.size()) {
                start = 0;
                end = Math.min(pageable.getPageSize(), logs.size());
            }
            
            List<CommunicationLog> pageContent = logs.subList(start, end);
            Page<CommunicationLog> page = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, logs.size());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", page.getContent());
            response.put("totalElements", page.getTotalElements());
            response.put("totalPages", page.getTotalPages());
            response.put("currentPage", page.getNumber());
            response.put("size", page.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching communication logs by email", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch communication logs");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get communication logs by recipient mobile
     */
    @GetMapping("/mobile/{mobile}")
    public ResponseEntity<Map<String, Object>> getLogsByMobile(
            @PathVariable String mobile,
            Pageable pageable) {
        try {
            List<CommunicationLog> logs = communicationLogService.getLogsByMobile(mobile);
            
            // Convert to page manually
            int start = Math.max(0, (int) pageable.getOffset());
            int end = Math.min((start + pageable.getPageSize()), logs.size());
            
            // Additional safety check to prevent IndexOutOfBoundsException
            if (start >= logs.size()) {
                start = 0;
                end = Math.min(pageable.getPageSize(), logs.size());
            }
            
            List<CommunicationLog> pageContent = logs.subList(start, end);
            Page<CommunicationLog> page = new org.springframework.data.domain.PageImpl<>(
                pageContent, pageable, logs.size());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", page.getContent());
            response.put("totalElements", page.getTotalElements());
            response.put("totalPages", page.getTotalPages());
            response.put("currentPage", page.getNumber());
            response.put("size", page.getSize());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching communication logs by mobile", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch communication logs");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get communication statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCommunicationStats(
            @RequestParam(defaultValue = "30") int days) {
        try {
            LocalDateTime since = LocalDateTime.now().minusDays(days);
            Object[] stats = communicationLogService.getCommunicationStats(since);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "delivered", stats[0],
                "failed", stats[1],
                "sent", stats[2],
                "total", stats[3],
                "period", days + " days"
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching communication statistics", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch communication statistics");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get failed communications that can be retried
     */
    @GetMapping("/retryable")
    public ResponseEntity<Map<String, Object>> getRetryableCommunications() {
        try {
            List<CommunicationLog> logs = communicationLogService.getRetryableCommunications();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", logs);
            response.put("count", logs.size());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching retryable communications", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch retryable communications");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Retry a failed communication
     */
    @PostMapping("/{logId}/retry")
    public ResponseEntity<Map<String, Object>> retryCommunication(@PathVariable Long logId) {
        try {
            communicationLogService.retryCommunication(logId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Communication scheduled for retry");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error retrying communication", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to retry communication");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get specific communication log by ID
     */
    @GetMapping("/{logId}")
    public ResponseEntity<Map<String, Object>> getLogById(@PathVariable Long logId) {
        try {
            return communicationLogService.getLogById(logId)
                .map(log -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("data", log);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("message", "Communication log not found");
                    return ResponseEntity.notFound().build();
                });
        } catch (Exception e) {
            log.error("Error fetching communication log by ID", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to fetch communication log");
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
