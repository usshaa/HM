package com.hotelmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

public class AnalyticsDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OccupancyData {
        private String period;
        private double occupancyRate;
        private int totalRooms;
        private int occupiedRooms;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueData {
        private String month;
        private int year;
        private double totalRevenue;
        private Map<String, Double> revenueByRoomType;
        private Map<String, Double> revenueByPaymentMode;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpiData {
        private double averageDailyRate;
        private double revPar;
        private double averageStayDuration;
        private double occupancyRate;
        private int totalBookings;
        private int totalCheckIns;
        private int totalCheckOuts;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendData {
        private List<String> labels;
        private List<Double> occupancyRates;
        private List<Double> revenues;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingSourceData {
        private String source;
        private int count;
        private double percentage;
    }
}
