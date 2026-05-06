package com.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "rooms", indexes = {
    @Index(name = "idx_room_number", columnList = "room_number"),
    @Index(name = "idx_room_type_status", columnList = "room_type_id, status"),
    @Index(name = "idx_room_floor", columnList = "floor")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomNumber;

    @Column(nullable = false)
    private Integer floor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomStatus status;

    @Column(nullable = false)
    private boolean isActive;

    private Long lastCleaned;

    @Column(columnDefinition = "TEXT")
    private String notes;

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
