package com.icastar.platform.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@EqualsAndHashCode(callSuper = true)
public class Payment extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment primary key
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_type", nullable = false)
    private PaymentType paymentType;

    @Column(name = "amount", nullable = false, columnDefinition = "DECIMAL(10,2)")
    private BigDecimal amount;

    @Column(name = "currency", nullable = false)
    private String currency = "INR";

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "payment_reference", unique = true)
    private String paymentReference;

    @Column(name = "gateway_transaction_id")
    private String gatewayTransactionId;

    @Column(name = "gateway_response", columnDefinition = "JSON")
    private String gatewayResponse; // JSON response from payment gateway

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "failure_reason")
    private String failureReason;

    @Column(name = "refund_amount", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal refundAmount;

    @Column(name = "refund_reference")
    private String refundReference;

    @Column(name = "refunded_at")
    private LocalDateTime refundedAt;

    @Column(name = "commission_amount", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal commissionAmount;

    @Column(name = "net_amount", columnDefinition = "DECIMAL(10,2)")
    private BigDecimal netAmount;

    public enum PaymentType {
        SUBSCRIPTION, MESSAGE_UNLOCK, JOB_BOOST, COMMISSION, REFUND
    }

    public enum PaymentMethod {
        RAZORPAY, STRIPE, UPI, CARD, NET_BANKING, WALLET
    }

    public enum PaymentStatus {
        PENDING, SUCCESS, FAILED, CANCELLED, REFUNDED, PARTIALLY_REFUNDED
    }
}
