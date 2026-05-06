package com.hotelmanagement.service;

import com.hotelmanagement.dto.RoomExtraDTO;
import com.hotelmanagement.entity.*;
import com.hotelmanagement.exception.ResourceNotFoundException;
import com.hotelmanagement.repository.ReservationRepository;
import com.hotelmanagement.repository.RoomExtraRepository;
import com.hotelmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomExtraService {

    private final RoomExtraRepository roomExtraRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;

    public Double getTotalExtrasForReservation(Long reservationId) {
        return roomExtraRepository.findByReservationId(reservationId)
                .stream()
                .mapToDouble(RoomExtra::getAmount)
                .sum();
    }

    public List<RoomExtraDTO> getExtrasByReservation(Long reservationId) {
        return roomExtraRepository.findByReservationId(reservationId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomExtraDTO addExtra(RoomExtraDTO dto) {
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        RoomExtra extra = new RoomExtra();
        extra.setReservation(reservation);
        extra.setExtraType(dto.getExtraType());
        extra.setDescription(dto.getDescription());
        extra.setAmount(dto.getAmount());
        extra.setChargeDate(dto.getChargeDate() != null ? dto.getChargeDate() : System.currentTimeMillis());

        if (dto.getAddedById() != null) {
            User staff = userRepository.findById(dto.getAddedById()).orElse(null);
            extra.setAddedBy(staff);
        }

        RoomExtra saved = roomExtraRepository.save(extra);
        log.info("Room extra added: {} for reservation {}", saved.getId(), dto.getReservationId());
        return convertToDTO(saved);
    }

    @Transactional
    public void deleteExtra(Long extraId) {
        RoomExtra extra = roomExtraRepository.findById(extraId)
                .orElseThrow(() -> new ResourceNotFoundException("Room extra not found"));
        roomExtraRepository.delete(extra);
        log.info("Room extra deleted: {}", extraId);
    }

    private RoomExtraDTO convertToDTO(RoomExtra extra) {
        return new RoomExtraDTO(
                extra.getId(),
                extra.getReservation().getId(),
                extra.getExtraType(),
                extra.getDescription(),
                extra.getAmount(),
                extra.getChargeDate(),
                extra.getAddedBy() != null ? extra.getAddedBy().getId() : null,
                extra.getAddedBy() != null ? extra.getAddedBy().getName() : null
        );
    }
}
