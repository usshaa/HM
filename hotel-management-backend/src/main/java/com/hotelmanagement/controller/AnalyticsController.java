package com.hotelmanagement.controller;

import com.hotelmanagement.dto.AnalyticsDTO;
import com.hotelmanagement.dto.ApiResponse;
import com.hotelmanagement.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/occupancy")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<List<AnalyticsDTO.OccupancyData>>> getOccupancy(
            @RequestParam(defaultValue = "daily") String period) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Occupancy data retrieved",
                analyticsService.getOccupancyData(period)));
    }

    @GetMapping("/revenue")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnalyticsDTO.RevenueData>> getRevenue(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        int m = month != null ? month : LocalDate.now().getMonthValue();
        int y = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(new ApiResponse<>(true, "Revenue data retrieved",
                analyticsService.getRevenueData(m, y)));
    }

    @GetMapping("/kpi")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<AnalyticsDTO.KpiData>> getKpi(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        int m = month != null ? month : LocalDate.now().getMonthValue();
        int y = year != null ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(new ApiResponse<>(true, "KPI data retrieved",
                analyticsService.getKpiData(m, y)));
    }

    @GetMapping("/trend")
    @PreAuthorize("hasRole('ADMIN') or hasRole('RECEPTIONIST')")
    public ResponseEntity<ApiResponse<AnalyticsDTO.TrendData>> getTrend(
            @RequestParam(defaultValue = "6") int months) {
        return ResponseEntity.ok(new ApiResponse<>(true, "Trend data retrieved",
                analyticsService.getTrendData(months)));
    }

    @GetMapping("/booking-sources")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<AnalyticsDTO.BookingSourceData>>> getBookingSources() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Booking source data retrieved",
                analyticsService.getBookingSourceData()));
    }
}
