package com.hotelmanagement.service;

import com.hotelmanagement.dto.RoomDTO;
import com.hotelmanagement.dto.RoomTypeDTO;
import com.hotelmanagement.entity.*;
import com.hotelmanagement.exception.ResourceNotFoundException;
import com.hotelmanagement.repository.RoomRepository;
import com.hotelmanagement.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    public Page<RoomTypeDTO> getAllRoomTypes(Pageable pageable) {
        return roomTypeRepository.findByIsActiveTrue(pageable).map(this::convertRoomTypeToDTO);
    }

    @Transactional
    public RoomTypeDTO createRoomType(RoomTypeDTO dto) {
        RoomType rt = new RoomType();
        rt.setTypeName(dto.getTypeName());
        rt.setDescription(dto.getDescription());
        rt.setBasePrice(dto.getBasePrice());
        rt.setMaxOccupancy(dto.getMaxOccupancy());
        rt.setBedType(dto.getBedType());
        rt.setAmenities(dto.getAmenities());
        rt.setPhotoUrl(dto.getPhotoUrl());
        rt.setActive(true);
        RoomType saved = roomTypeRepository.save(rt);
        log.info("Room type created: {}", saved.getId());
        return convertRoomTypeToDTO(saved);
    }

    @Transactional
    public RoomTypeDTO updateRoomType(Long id, RoomTypeDTO dto) {
        RoomType rt = roomTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
        if (dto.getTypeName() != null) rt.setTypeName(dto.getTypeName());
        if (dto.getDescription() != null) rt.setDescription(dto.getDescription());
        if (dto.getBasePrice() != null) rt.setBasePrice(dto.getBasePrice());
        if (dto.getMaxOccupancy() != null) rt.setMaxOccupancy(dto.getMaxOccupancy());
        if (dto.getBedType() != null) rt.setBedType(dto.getBedType());
        if (dto.getAmenities() != null) rt.setAmenities(dto.getAmenities());
        if (dto.getPhotoUrl() != null) rt.setPhotoUrl(dto.getPhotoUrl());
        RoomType saved = roomTypeRepository.save(rt);
        return convertRoomTypeToDTO(saved);
    }

    @Transactional
    public void deleteRoomType(Long id) {
        RoomType rt = roomTypeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
        rt.setActive(false);
        roomTypeRepository.save(rt);
        log.info("Room type deactivated: {}", id);
    }

    public Page<RoomDTO> getAllRooms(Pageable pageable) {
        return roomRepository.findByIsActiveTrue(pageable).map(this::convertRoomToDTO);
    }

    @Transactional
    public RoomDTO createRoom(RoomDTO dto) {
        RoomType roomType = roomTypeRepository.findById(dto.getRoomTypeId()).orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setFloor(dto.getFloor());
        room.setRoomType(roomType);
        room.setStatus(RoomStatus.AVAILABLE);
        room.setActive(true);
        room.setNotes(dto.getNotes());
        Room saved = roomRepository.save(room);
        log.info("Room created: {}", saved.getRoomNumber());
        return convertRoomToDTO(saved);
    }

    @Transactional
    public RoomDTO updateRoom(Long id, RoomDTO dto) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        if (dto.getRoomNumber() != null) room.setRoomNumber(dto.getRoomNumber());
        if (dto.getFloor() != null) room.setFloor(dto.getFloor());
        if (dto.getNotes() != null) room.setNotes(dto.getNotes());
        if (dto.getRoomTypeId() != null) {
            RoomType rt = roomTypeRepository.findById(dto.getRoomTypeId()).orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
            room.setRoomType(rt);
        }
        Room saved = roomRepository.save(room);
        return convertRoomToDTO(saved);
    }

    @Transactional
    public RoomDTO updateRoomStatus(Long roomId, RoomStatus newStatus) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        room.setStatus(newStatus);
        if (newStatus == RoomStatus.CLEAN) room.setLastCleaned(System.currentTimeMillis());
        Room updated = roomRepository.save(room);
        log.info("Room status updated: {} -> {}", roomId, newStatus);
        return convertRoomToDTO(updated);
    }

    @Transactional
    public void deleteRoom(Long id) {
        Room room = roomRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        room.setActive(false);
        roomRepository.save(room);
        log.info("Room soft-deleted: {}", id);
    }

    public Page<RoomDTO> searchAvailableRooms(Long roomTypeId, Long checkInDate, Long checkOutDate, Pageable pageable) {
        roomTypeRepository.findById(roomTypeId).orElseThrow(() -> new ResourceNotFoundException("Room type not found"));
        var available = roomRepository.findAvailableRooms(roomTypeId, RoomStatus.AVAILABLE);
        var dtos = available.stream().map(this::convertRoomToDTO).collect(Collectors.toList());
        return new PageImpl<>(dtos, pageable, dtos.size());
    }

    private RoomTypeDTO convertRoomTypeToDTO(RoomType rt) {
        return new RoomTypeDTO(rt.getId(), rt.getTypeName(), rt.getDescription(), rt.getBasePrice(), rt.getMaxOccupancy(), rt.getBedType(), rt.getAmenities(), rt.getPhotoUrl(), rt.isActive());
    }

    private RoomDTO convertRoomToDTO(Room room) {
        return new RoomDTO(room.getId(), room.getRoomNumber(), room.getFloor(), room.getRoomType().getId(),
                new RoomDTO.RoomType(room.getRoomType().getId(), room.getRoomType().getTypeName(), room.getRoomType().getBasePrice()),
                room.getStatus(), room.isActive(), room.getLastCleaned(), room.getNotes());
    }
}
