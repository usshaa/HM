package com.hotelmanagement.controller;

import com.hotelmanagement.dto.ApiResponse;
import com.hotelmanagement.dto.LoginRequest;
import com.hotelmanagement.dto.LoginResponse;
import com.hotelmanagement.dto.UserDTO;
import com.hotelmanagement.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody UserDTO request) {
        UserDTO user = authService.registerGuest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Registration successful", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Login successful", response));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestHeader("Authorization") String token) {
        String refreshToken = token.substring(7);
        String newAccessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new ApiResponse<>(true, "Token refreshed", newAccessToken));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @RequestParam String email,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        authService.changePassword(email, oldPassword, newPassword);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password changed successfully", null));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Service is running", "OK"));
    }
}
