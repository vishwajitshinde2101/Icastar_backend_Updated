package com.icastar.platform.controller;

import com.icastar.platform.dto.admin.AccountManagementResponseDto;
import com.icastar.platform.dto.admin.AccountStatusChangeDto;
import com.icastar.platform.entity.User;
import com.icastar.platform.service.AdminAccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/accounts")
@RequiredArgsConstructor
@Slf4j
public class AdminAccountController {
    
    private final AdminAccountService adminAccountService;
    
    /**
     * Get all users with pagination and filtering
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String search,
            Pageable pageable,
            Authentication authentication) {
        
        try {
            User admin = (User) authentication.getPrincipal();
            Page<User> users = adminAccountService.getAllUsers(status, role, search, pageable, admin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", users.getContent());
            response.put("totalElements", users.getTotalElements());
            response.put("totalPages", users.getTotalPages());
            response.put("currentPage", users.getNumber());
            response.put("size", users.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting users: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get users: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get user details by ID
     */
    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getUserById(
            @PathVariable Long userId,
            Authentication authentication) {
        
        try {
            User admin = (User) authentication.getPrincipal();
            AccountManagementResponseDto user = adminAccountService.getUserById(userId, admin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", user);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting user: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get user: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Deactivate user account
     */
    @PostMapping("/{userId}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivateAccount(
            @PathVariable Long userId,
            @Valid @RequestBody AccountStatusChangeDto request,
            HttpServletRequest httpRequest,
            Authentication authentication) {
        
        try {
            User admin = (User) authentication.getPrincipal();
            request.setUserId(userId);
            request.setIpAddress(getClientIpAddress(httpRequest));
            request.setUserAgent(httpRequest.getHeader("User-Agent"));
            
            AccountManagementResponseDto result = adminAccountService.deactivateAccount(request, admin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account deactivated successfully");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deactivating account: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to deactivate account: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Reactivate user account
     */
    @PostMapping("/{userId}/reactivate")
    public ResponseEntity<Map<String, Object>> reactivateAccount(
            @PathVariable Long userId,
            @Valid @RequestBody AccountStatusChangeDto request,
            HttpServletRequest httpRequest,
            Authentication authentication) {
        
        try {
            User admin = (User) authentication.getPrincipal();
            request.setUserId(userId);
            request.setIpAddress(getClientIpAddress(httpRequest));
            request.setUserAgent(httpRequest.getHeader("User-Agent"));
            
            AccountManagementResponseDto result = adminAccountService.reactivateAccount(request, admin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account reactivated successfully");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error reactivating account: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to reactivate account: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Suspend user account
     */
    @PostMapping("/{userId}/suspend")
    public ResponseEntity<Map<String, Object>> suspendAccount(
            @PathVariable Long userId,
            @Valid @RequestBody AccountStatusChangeDto request,
            HttpServletRequest httpRequest,
            Authentication authentication) {
        
        try {
            User admin = (User) authentication.getPrincipal();
            request.setUserId(userId);
            request.setIpAddress(getClientIpAddress(httpRequest));
            request.setUserAgent(httpRequest.getHeader("User-Agent"));
            
            AccountManagementResponseDto result = adminAccountService.suspendAccount(request, admin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account suspended successfully");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error suspending account: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to suspend account: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Ban user account
     */
    @PostMapping("/{userId}/ban")
    public ResponseEntity<Map<String, Object>> banAccount(
            @PathVariable Long userId,
            @Valid @RequestBody AccountStatusChangeDto request,
            HttpServletRequest httpRequest,
            Authentication authentication) {
        
        try {
            User admin = (User) authentication.getPrincipal();
            request.setUserId(userId);
            request.setIpAddress(getClientIpAddress(httpRequest));
            request.setUserAgent(httpRequest.getHeader("User-Agent"));
            
            AccountManagementResponseDto result = adminAccountService.banAccount(request, admin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Account banned successfully");
            response.put("data", result);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error banning account: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to ban account: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get account management logs
     */
    @GetMapping("/{userId}/logs")
    public ResponseEntity<Map<String, Object>> getAccountLogs(
            @PathVariable Long userId,
            Pageable pageable,
            Authentication authentication) {
        
        try {
            User admin = (User) authentication.getPrincipal();
            Page<Map<String, Object>> logs = adminAccountService.getAccountLogs(userId, pageable, admin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", logs.getContent());
            response.put("totalElements", logs.getTotalElements());
            response.put("totalPages", logs.getTotalPages());
            response.put("currentPage", logs.getNumber());
            response.put("size", logs.getSize());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting account logs: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get account logs: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * Get account statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getAccountStatistics(Authentication authentication) {
        
        try {
            User admin = (User) authentication.getPrincipal();
            Map<String, Object> statistics = adminAccountService.getAccountStatistics(admin);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", statistics);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error getting account statistics: {}", e.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to get account statistics: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}
