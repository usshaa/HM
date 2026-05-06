package com.hotelmanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "housekeeping_tasks", indexes = {
    @Index(name = "idx_room_id", columnList = "room_id"),
    @Index(name = "idx_assigned_to", columnList = "assigned_to_id"),
    @Index(name = "idx_status_date", columnList = "status, scheduled_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HousekeepingTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id", nullable = false)
    private User assignedTo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskType taskType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Priority priority;

    @Column(nullable = false)
    private Long scheduledDate;

    private Long completedAt;

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
