package com.icastar.platform.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "account_management_log")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class AccountManagementLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    private AccountAction action;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false)
    private AccountStatus previousStatus;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private AccountStatus newStatus;
    
    @Column(name = "reason", columnDefinition = "TEXT")
    private String reason;
    
    @Column(name = "admin_notes", columnDefinition = "TEXT")
    private String adminNotes;
    
    @Column(name = "ip_address", length = 45)
    private String ipAddress;
    
    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Enums
    public enum AccountAction {
        ACTIVATE, DEACTIVATE, SUSPEND, UNSUSPEND, BAN, UNBAN, VERIFY, UNVERIFY
    }
    
    public enum AccountStatus {
        ACTIVE, INACTIVE, SUSPENDED, BANNED, PENDING_VERIFICATION
    }
    
    // Static factory method for creating log entries
    public static AccountManagementLog info(String reason, AccountAction action, Long userId, Long adminId) {
        return AccountManagementLog.builder()
                .action(action)
                .reason(reason)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
