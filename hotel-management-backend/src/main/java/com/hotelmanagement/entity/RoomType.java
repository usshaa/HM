package com.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "room_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String typeName;

    private String description;

    @Column(nullable = false)
    private Double basePrice;

    @Column(nullable = false)
    private Integer maxOccupancy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BedType bedType;

    @Column(columnDefinition = "JSON")
    private String amenities; // JSON array

    private String photoUrl;

    @Column(nullable = false)
    private boolean isActive;

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
