package com.hotelmanagement.controller;

import com.hotelmanagement.dto.ApiResponse;
import com.hotelmanagement.dto.ReservationDTO;
import com.hotelmanagement.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('GUEST')")
    public ResponseEntity<ApiResponse<ReservationDTO>> createReservation(@Valid @RequestBody ReservationDTO request) {
        ReservationDTO created = reservationService.createReservation(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Reservation created", created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReservationDTO>> modifyReservation(@PathVariable Long id, @Valid @RequestBody ReservationDTO request) {
        ReservationDTO updated = reservationService.modifyReservation(id, request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservation updated", updated));
    }

    @PatchMapping("/{id}/confirm")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<ReservationDTO>> confirmReservation(@PathVariable Long id) {
        ReservationDTO confirmed = reservationService.confirmReservation(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservation confirmed", confirmed));
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('GUEST')")
    public ResponseEntity<ApiResponse<ReservationDTO>> cancelReservation(@PathVariable Long id) {
        ReservationDTO cancelled = reservationService.cancelReservation(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservation cancelled", cancelled));
    }

    @PostMapping("/{id}/checkin")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<ReservationDTO>> checkIn(@PathVariable Long id, @RequestParam Long roomId) {
        ReservationDTO checkedIn = reservationService.checkIn(id, roomId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Guest checked in", checkedIn));
    }

    @PostMapping("/{id}/checkout")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<ReservationDTO>> checkOut(@PathVariable Long id) {
        ReservationDTO checkedOut = reservationService.checkOut(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Guest checked out", checkedOut));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ReservationDTO>>> getAllReservations(Pageable pageable) {
        Page<ReservationDTO> reservations = reservationService.getAllReservations(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservations retrieved", reservations));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationDTO>> getReservation(@PathVariable Long id) {
        ReservationDTO reservation = reservationService.getReservationById(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Reservation retrieved", reservation));
    }

    @GetMapping("/guest/{guestId}")
    @PreAuthorize("hasRole('GUEST') or hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<Page<ReservationDTO>>> getGuestReservations(@PathVariable Long guestId, Pageable pageable) {
        Page<ReservationDTO> reservations = reservationService.getGuestReservations(guestId, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Guest reservations retrieved", reservations));
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<byte[]> exportCsv() {
        String csv = reservationService.exportReservationsCsv();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", "reservations.csv");
        return new ResponseEntity<>(csv.getBytes(), headers, HttpStatus.OK);
    }
}
