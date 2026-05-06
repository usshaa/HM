package com.hotelmanagement.controller;

import com.hotelmanagement.dto.ApiResponse;
import com.hotelmanagement.dto.HousekeepingTaskDTO;
import com.hotelmanagement.dto.MaintenanceRequestDTO;
import com.hotelmanagement.entity.MaintenanceStatus;
import com.hotelmanagement.entity.TaskStatus;
import com.hotelmanagement.service.HousekeepingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/housekeeping")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class HousekeepingController {

    private final HousekeepingService housekeepingService;

    @GetMapping("/tasks")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HOUSEKEEPING') or hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<Page<HousekeepingTaskDTO>>> getTasks(
            @RequestParam(required = false) Long date,
            @RequestParam(required = false) Long assignedTo,
            Pageable pageable) {
        Page<HousekeepingTaskDTO> tasks = housekeepingService.getTasks(date, assignedTo, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Tasks retrieved", tasks));
    }

    @PostMapping("/tasks")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<HousekeepingTaskDTO>> createTask(@Valid @RequestBody HousekeepingTaskDTO request) {
        HousekeepingTaskDTO created = housekeepingService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Task created", created));
    }

    @PatchMapping("/tasks/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HOUSEKEEPING')")
    public ResponseEntity<ApiResponse<HousekeepingTaskDTO>> updateTaskStatus(
            @PathVariable Long id,
            @RequestParam TaskStatus status) {
        HousekeepingTaskDTO updated = housekeepingService.updateTaskStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Task status updated", updated));
    }

    @PostMapping("/maintenance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HOUSEKEEPING') or hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<MaintenanceRequestDTO>> createMaintenance(@Valid @RequestBody MaintenanceRequestDTO request) {
        MaintenanceRequestDTO created = housekeepingService.createMaintenanceRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Maintenance request created", created));
    }

    @PatchMapping("/maintenance/{id}/status")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<MaintenanceRequestDTO>> updateMaintenanceStatus(
            @PathVariable Long id,
            @RequestParam MaintenanceStatus status) {
        MaintenanceRequestDTO updated = housekeepingService.updateMaintenanceStatus(id, status);
        return ResponseEntity.ok(new ApiResponse<>(true, "Maintenance status updated", updated));
    }

    @GetMapping("/maintenance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('HOUSEKEEPING') or hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<Page<MaintenanceRequestDTO>>> getMaintenanceRequests(
            @RequestParam(required = false) MaintenanceStatus status,
            Pageable pageable) {
        Page<MaintenanceRequestDTO> requests = housekeepingService.getMaintenanceRequests(status, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Maintenance requests retrieved", requests));
    }
}
