package com.hotelmanagement.controller;

import com.hotelmanagement.dto.ApiResponse;
import com.hotelmanagement.dto.RoomExtraDTO;
import com.hotelmanagement.service.RoomExtraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/extras")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class RoomExtraController {

    private final RoomExtraService roomExtraService;

    @GetMapping("/reservation/{reservationId}")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN') or hasRole('GUEST')")
    public ResponseEntity<ApiResponse<List<RoomExtraDTO>>> getExtras(@PathVariable Long reservationId) {
        List<RoomExtraDTO> extras = roomExtraService.getExtrasByReservation(reservationId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Extras retrieved", extras));
    }

    @PostMapping
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<RoomExtraDTO>> addExtra(@Valid @RequestBody RoomExtraDTO request) {
        RoomExtraDTO created = roomExtraService.addExtra(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Extra charge added", created));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteExtra(@PathVariable Long id) {
        roomExtraService.deleteExtra(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Extra charge removed", null));
    }
}
