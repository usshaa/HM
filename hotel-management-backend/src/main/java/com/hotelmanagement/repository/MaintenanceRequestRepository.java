package com.hotelmanagement.repository;

import com.hotelmanagement.entity.MaintenanceRequest;
import com.hotelmanagement.entity.MaintenanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    Page<MaintenanceRequest> findByStatus(MaintenanceStatus status, Pageable pageable);
    Page<MaintenanceRequest> findByRoomId(Long roomId, Pageable pageable);
}
