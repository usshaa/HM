package com.hotelmanagement.controller;

import com.hotelmanagement.dto.ApiResponse;
import com.hotelmanagement.dto.RoomDTO;
import com.hotelmanagement.dto.RoomTypeDTO;
import com.hotelmanagement.entity.RoomStatus;
import com.hotelmanagement.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/types")
    public ResponseEntity<ApiResponse<Page<RoomTypeDTO>>> getAllRoomTypes(Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Room types retrieved", roomService.getAllRoomTypes(pageable)));
    }

    @PostMapping("/types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomTypeDTO>> createRoomType(@Valid @RequestBody RoomTypeDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Room type created", roomService.createRoomType(request)));
    }

    @PutMapping("/types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomTypeDTO>> updateRoomType(@PathVariable Long id, @Valid @RequestBody RoomTypeDTO request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Room type updated", roomService.updateRoomType(id, request)));
    }

    @DeleteMapping("/types/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteRoomType(@PathVariable Long id) {
        roomService.deleteRoomType(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Room type deactivated", null));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<RoomDTO>>> getAllRooms(Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Rooms retrieved", roomService.getAllRooms(pageable)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomDTO>> createRoom(@Valid @RequestBody RoomDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Room created", roomService.createRoom(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<RoomDTO>> updateRoom(@PathVariable Long id, @Valid @RequestBody RoomDTO request) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Room updated", roomService.updateRoom(id, request)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<RoomDTO>> updateRoomStatus(@PathVariable Long id, @RequestParam RoomStatus status) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Room status updated", roomService.updateRoomStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Room deactivated", null));
    }

    @GetMapping("/available")
    public ResponseEntity<ApiResponse<Page<RoomDTO>>> searchAvailableRooms(
            @RequestParam Long roomTypeId, @RequestParam Long checkInDate, @RequestParam Long checkOutDate, Pageable pageable) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Available rooms retrieved", roomService.searchAvailableRooms(roomTypeId, checkInDate, checkOutDate, pageable)));
    }
}
