package com.hotelmanagement.repository;

import com.hotelmanagement.entity.Room;
import com.hotelmanagement.entity.RoomStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(String roomNumber);
    Page<Room> findByIsActiveTrue(Pageable pageable);
    Page<Room> findByStatusAndIsActiveTrue(RoomStatus status, Pageable pageable);
    Page<Room> findByRoomTypeIdAndIsActiveTrue(Long roomTypeId, Pageable pageable);
    Page<Room> findByFloorAndIsActiveTrue(Integer floor, Pageable pageable);
    
    @Query("SELECT r FROM Room r WHERE r.roomType.id = :roomTypeId AND r.status = :status AND r.isActive = true")
    List<Room> findAvailableRooms(@Param("roomTypeId") Long roomTypeId, @Param("status") RoomStatus status);
}
