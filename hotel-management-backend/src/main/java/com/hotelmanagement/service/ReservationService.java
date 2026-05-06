package com.hotelmanagement.service;

import com.hotelmanagement.dto.ReservationDTO;
import com.hotelmanagement.entity.*;
import com.hotelmanagement.exception.BadRequestException;
import com.hotelmanagement.exception.ResourceNotFoundException;
import com.hotelmanagement.repository.ReservationRepository;
import com.hotelmanagement.repository.RoomRepository;
import com.hotelmanagement.repository.RoomTypeRepository;
import com.hotelmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final UserRepository userRepository;
    private final InvoiceService invoiceService;

    @Transactional
    public ReservationDTO createReservation(ReservationDTO dto) {
        User guest = userRepository.findById(dto.getGuestId())
                .orElseThrow(() -> new ResourceNotFoundException("Guest not found"));
        RoomType roomType = roomTypeRepository.findById(dto.getRoomTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
        if (dto.getCheckOutDate() <= dto.getCheckInDate()) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
        validateAvailability(roomType.getId(), dto.getCheckInDate(), dto.getCheckOutDate());

        Reservation reservation = new Reservation();
        reservation.setGuest(guest);
        reservation.setRoomType(roomType);
        reservation.setCheckInDate(dto.getCheckInDate());
        reservation.setCheckOutDate(dto.getCheckOutDate());
        reservation.setNumAdults(dto.getNumAdults());
        reservation.setNumChildren(dto.getNumChildren() != null ? dto.getNumChildren() : 0);
        reservation.setStatus(ReservationStatus.PENDING);
        reservation.setSource(dto.getSource() != null ? dto.getSource() : BookingSource.PORTAL);
        reservation.setSpecialRequests(dto.getSpecialRequests());
        reservation.setEarlyCheckInRequested(dto.getEarlyCheckInRequested());
        reservation.setLateCheckOutRequested(dto.getLateCheckOutRequested());

        long nights = (dto.getCheckOutDate() - dto.getCheckInDate()) / (24 * 60 * 60 * 1000);
        if (nights < 1) nights = 1;
        reservation.setTotalAmount(roomType.getBasePrice() * nights);

        Reservation saved = reservationRepository.save(reservation);
        log.info("Reservation created: {}", saved.getId());
        return convertToDTO(saved);
    }

    @Transactional
    public ReservationDTO modifyReservation(Long id, ReservationDTO dto) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        if (reservation.getStatus() == ReservationStatus.CHECKED_IN || reservation.getStatus() == ReservationStatus.CHECKED_OUT) {
            throw new BadRequestException("Cannot modify a checked-in or checked-out reservation");
        }
        if (dto.getCheckInDate() != null) reservation.setCheckInDate(dto.getCheckInDate());
        if (dto.getCheckOutDate() != null) reservation.setCheckOutDate(dto.getCheckOutDate());
        if (dto.getRoomTypeId() != null) {
            RoomType rt = roomTypeRepository.findById(dto.getRoomTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
            reservation.setRoomType(rt);
        }
        if (dto.getSpecialRequests() != null) reservation.setSpecialRequests(dto.getSpecialRequests());
        if (dto.getNumAdults() != null) reservation.setNumAdults(dto.getNumAdults());
        if (dto.getNumChildren() != null) reservation.setNumChildren(dto.getNumChildren());

        long nights = (reservation.getCheckOutDate() - reservation.getCheckInDate()) / (24 * 60 * 60 * 1000);
        if (nights < 1) nights = 1;
        reservation.setTotalAmount(reservation.getRoomType().getBasePrice() * nights);

        Reservation updated = reservationRepository.save(reservation);
        log.info("Reservation modified: {}", id);
        return convertToDTO(updated);
    }

    @Transactional
    public ReservationDTO confirmReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new BadRequestException("Only pending reservations can be confirmed");
        }
        reservation.setStatus(ReservationStatus.CONFIRMED);
        Reservation updated = reservationRepository.save(reservation);
        log.info("Reservation confirmed: {}", reservationId);
        return convertToDTO(updated);
    }

    @Transactional
    public ReservationDTO cancelReservation(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            throw new BadRequestException("Cannot cancel a checked-in reservation. Please check out first.");
        }
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BadRequestException("Reservation is already cancelled");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        if (reservation.getRoom() != null) {
            reservation.getRoom().setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(reservation.getRoom());
        }
        Reservation updated = reservationRepository.save(reservation);
        log.info("Reservation cancelled: {}", reservationId);
        return convertToDTO(updated);
    }

    @Transactional
    public ReservationDTO checkIn(Long reservationId, Long roomId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BadRequestException("Cannot check in a cancelled reservation");
        }
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        reservation.setRoom(room);
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        room.setStatus(RoomStatus.OCCUPIED);
        roomRepository.save(room);
        Reservation updated = reservationRepository.save(reservation);
        log.info("Guest checked in: reservation {}, room {}", reservationId, roomId);
        return convertToDTO(updated);
    }

    @Transactional
    public ReservationDTO checkOut(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw new BadRequestException("Only checked-in reservations can be checked out");
        }
        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        if (reservation.getRoom() != null) {
            reservation.getRoom().setStatus(RoomStatus.DIRTY);
            roomRepository.save(reservation.getRoom());
        }
        Reservation updated = reservationRepository.save(reservation);
        log.info("Guest checked out: reservation {}", reservationId);
        return convertToDTO(updated);
    }

    public ReservationDTO getReservationById(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        return convertToDTO(reservation);
    }

    public Page<ReservationDTO> getGuestReservations(Long guestId, Pageable pageable) {
        return reservationRepository.findByGuestId(guestId, pageable).map(this::convertToDTO);
    }

    public Page<ReservationDTO> getAllReservations(Pageable pageable) {
        return reservationRepository.findAll(pageable).map(this::convertToDTO);
    }

    public String exportReservationsCsv() {
        List<Reservation> all = reservationRepository.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("ID,Guest,RoomType,CheckIn,CheckOut,Status,Source,TotalAmount\n");
        for (Reservation r : all) {
            sb.append(r.getId()).append(",")
              .append(r.getGuest().getName()).append(",")
              .append(r.getRoomType().getTypeName()).append(",")
              .append(r.getCheckInDate()).append(",")
              .append(r.getCheckOutDate()).append(",")
              .append(r.getStatus()).append(",")
              .append(r.getSource()).append(",")
              .append(r.getTotalAmount()).append("\n");
        }
        return sb.toString();
    }

    private void validateAvailability(Long roomTypeId, Long checkInDate, Long checkOutDate) {
        var availableRooms = roomRepository.findAvailableRooms(roomTypeId, RoomStatus.AVAILABLE);
        if (availableRooms.isEmpty()) {
            throw new BadRequestException("No rooms available for selected type and dates");
        }
    }

    private ReservationDTO convertToDTO(Reservation reservation) {
        return new ReservationDTO(
                reservation.getId(),
                reservation.getGuest().getId(),
                reservation.getGuest().getName(),
                reservation.getRoom() != null ? reservation.getRoom().getId() : null,
                reservation.getRoom() != null ? reservation.getRoom().getRoomNumber() : null,
                reservation.getRoomType().getId(),
                reservation.getRoomType().getTypeName(),
                reservation.getCheckInDate(),
                reservation.getCheckOutDate(),
                reservation.getNumAdults(),
                reservation.getNumChildren(),
                reservation.getStatus(),
                reservation.getSource(),
                reservation.getSpecialRequests(),
                reservation.getTotalAmount(),
                reservation.getEarlyCheckInRequested(),
                reservation.getLateCheckOutRequested(),
                reservation.getCreatedAt()
        );
    }
}
