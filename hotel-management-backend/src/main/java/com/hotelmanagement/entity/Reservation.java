package com.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reservations", indexes = {
    @Index(name = "idx_guest_id", columnList = "guest_id"),
    @Index(name = "idx_room_id", columnList = "room_id"),
    @Index(name = "idx_check_in_date", columnList = "check_in_date"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @Column(nullable = false)
    private Long checkInDate;

    @Column(nullable = false)
    private Long checkOutDate;

    @Column(nullable = false)
    private Integer numAdults;

    private Integer numChildren;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingSource source;

    @Column(columnDefinition = "TEXT")
    private String specialRequests;

    @Column(nullable = false)
    private Double totalAmount;

    private Boolean earlyCheckInRequested;

    private Boolean lateCheckOutRequested;

    @Column(nullable = false, updatable = false)
    private Long createdAt;

    @Column(nullable = false)
    private Long updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
        updatedAt = System.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = System.currentTimeMillis();
    }
}
