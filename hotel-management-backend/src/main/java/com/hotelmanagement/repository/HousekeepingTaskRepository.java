package com.hotelmanagement.repository;

import com.hotelmanagement.entity.HousekeepingTask;
import com.hotelmanagement.entity.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HousekeepingTaskRepository extends JpaRepository<HousekeepingTask, Long> {
    Page<HousekeepingTask> findByAssignedToId(Long assignedToId, Pageable pageable);
    Page<HousekeepingTask> findByStatus(TaskStatus status, Pageable pageable);
    Page<HousekeepingTask> findByRoomId(Long roomId, Pageable pageable);
}
