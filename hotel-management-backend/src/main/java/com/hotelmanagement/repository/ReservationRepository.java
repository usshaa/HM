package com.hotelmanagement.repository;

import com.hotelmanagement.entity.Reservation;
import com.hotelmanagement.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByGuestId(Long guestId, Pageable pageable);
    Page<Reservation> findByStatus(ReservationStatus status, Pageable pageable);
    List<Reservation> findByGuestIdAndStatus(Long guestId, ReservationStatus status);
    
    @Query("SELECT r FROM Reservation r WHERE r.room.id = :roomId AND r.status NOT IN ('CHECKED_OUT', 'CANCELLED')")
    List<Reservation> findActiveReservationsByRoom(@Param("roomId") Long roomId);
}
