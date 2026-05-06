package com.hotelmanagement.controller;

import com.hotelmanagement.dto.AuditLogDTO;
import com.hotelmanagement.dto.ApiResponse;
import com.hotelmanagement.dto.UserDTO;
import com.hotelmanagement.service.AdminService;
import com.hotelmanagement.service.AnalyticsService;
import com.hotelmanagement.util.ExportUtil;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(Pageable pageable) {
        Page<UserDTO> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Users retrieved", users));
    }

    @PatchMapping("/users/{id}/activate")
    public ResponseEntity<ApiResponse<UserDTO>> toggleUserActive(
            @PathVariable Long id,
            @RequestParam boolean activate) {
        UserDTO updated = adminService.activateDeactivateUser(id, activate);
        return ResponseEntity.ok(new ApiResponse<>(true, "User status updated", updated));
    }

    @PostMapping("/staff")
    public ResponseEntity<ApiResponse<UserDTO>> createStaff(@Valid @RequestBody UserDTO request) {
        UserDTO created = adminService.createStaffAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Staff account created", created));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<Page<AuditLogDTO>>> getAuditLogs(
            @RequestParam(required = false) Long from,
            @RequestParam(required = false) Long to,
            Pageable pageable) {
        Page<AuditLogDTO> logs = adminService.getAuditLogs(from, to, pageable);
        return ResponseEntity.ok(new ApiResponse<>(true, "Audit logs retrieved", logs));
    }
}
