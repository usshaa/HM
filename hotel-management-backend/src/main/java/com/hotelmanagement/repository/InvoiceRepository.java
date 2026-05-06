package com.hotelmanagement.repository;

import com.hotelmanagement.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    Optional<Invoice> findByReservationId(Long reservationId);
    Page<Invoice> findAll(Pageable pageable);
}
