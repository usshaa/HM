package com.hotelmanagement.repository;

import com.hotelmanagement.entity.RoomType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Long> {
    Page<RoomType> findByIsActiveTrue(Pageable pageable);
    Optional<RoomType> findByTypeName(String typeName);
}
