package com.hotelmanagement.repository;

import com.hotelmanagement.entity.RoomExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomExtraRepository extends JpaRepository<RoomExtra, Long> {
    List<RoomExtra> findByReservationId(Long reservationId);
}
