package com.icastar.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
@Data
@EqualsAndHashCode(callSuper = true)
public class AuditLog extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "old_values", columnDefinition = "JSON")
    private String oldValues; // JSON representation of old values

    @Column(name = "new_values", columnDefinition = "JSON")
    private String newValues; // JSON representation of new values

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(name = "description")
    private String description;

    public enum ActionType {
        CREATE, READ, UPDATE, DELETE, LOGIN, LOGOUT, EXPORT, IMPORT
    }
}
