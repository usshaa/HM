package com.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "invoices", indexes = {
    @Index(name = "idx_reservation_id", columnList = "reservation_id"),
    @Index(name = "idx_invoice_status", columnList = "payment_status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false, unique = true)
    private Reservation reservation;

    @Column(nullable = false)
    private Double roomCharges;

    @Column(nullable = false)
    private Double extraCharges;

    @Column(nullable = false)
    private Double discountAmount;

    @Column(nullable = false)
    private Double taxAmount;

    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private Double paidAmount;

    @Column(nullable = false)
    private Double balanceDue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMode paymentMode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus paymentStatus;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, updatable = false)
    private Long generatedAt;

    @Column(nullable = false)
    private Long updatedAt;

    @PrePersist
    protected void onCreate() {
        generatedAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }
}
