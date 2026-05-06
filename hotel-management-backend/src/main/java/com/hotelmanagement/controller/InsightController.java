package com.hotelmanagement.controller;

import com.hotelmanagement.dto.ApiResponse;
import com.hotelmanagement.service.InsightService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/insights")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class InsightController {

    private final InsightService insightService;

    @PostMapping("/room-recommendations")
    @PreAuthorize("hasRole('GUEST') or hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> getRoomRecommendations(@RequestBody Map<String, String> request) {
        String guestProfile = request.getOrDefault("guestProfile", "General guest looking for a comfortable stay");
        String recommendations = insightService.getRoomRecommendations(guestProfile);
        return ResponseEntity.ok(new ApiResponse<>(true, "Recommendations generated", recommendations));
    }

    @PostMapping("/upsell-tips")
    @PreAuthorize("hasRole('RECEPTIONIST') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> getUpsellTips(@RequestBody Map<String, String> request) {
        String guestProfile = request.getOrDefault("guestProfile", "Business traveler");
        String tips = insightService.getUpsellTips(guestProfile);
        return ResponseEntity.ok(new ApiResponse<>(true, "Upsell tips generated", tips));
    }
}
