package com.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "room_extras", indexes = {
    @Index(name = "idx_reservation_id", columnList = "reservation_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExtraType extraType;

    private String description;

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private Long chargeDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "added_by_id")
    private User addedBy;

    @Column(nullable = false, updatable = false)
    private Long createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = System.currentTimeMillis();
    }
}
