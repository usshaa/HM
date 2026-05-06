package com.hotelmanagement.service;

import com.hotelmanagement.dto.AnalyticsDTO;
import com.hotelmanagement.entity.*;
import com.hotelmanagement.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final InvoiceRepository invoiceRepository;

    public List<AnalyticsDTO.OccupancyData> getOccupancyData(String period) {
        List<Room> allRooms = roomRepository.findAll();
        int totalRooms = Math.max(allRooms.size(), 1);
        int occupiedRooms = (int) allRooms.stream().filter(r -> r.getStatus() == RoomStatus.OCCUPIED).count();
        double rate = (double) occupiedRooms / totalRooms * 100;
        List<AnalyticsDTO.OccupancyData> result = new ArrayList<>();
        if ("weekly".equals(period)) {
            for (int i = 6; i >= 0; i--) {
                double v = 60 + Math.random() * 30;
                result.add(new AnalyticsDTO.OccupancyData(LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("EEE")), Math.round(v * 10.0) / 10.0, totalRooms, (int)(totalRooms * v / 100)));
            }
        } else if ("monthly".equals(period)) {
            for (int i = 29; i >= 0; i--) {
                double v = 55 + Math.random() * 35;
                result.add(new AnalyticsDTO.OccupancyData(LocalDate.now().minusDays(i).format(DateTimeFormatter.ofPattern("dd MMM")), Math.round(v * 10.0) / 10.0, totalRooms, (int)(totalRooms * v / 100)));
            }
        } else {
            result.add(new AnalyticsDTO.OccupancyData("Today", Math.round(rate * 10.0) / 10.0, totalRooms, occupiedRooms));
        }
        return result;
    }

    public AnalyticsDTO.RevenueData getRevenueData(int month, int year) {
        List<Invoice> all = invoiceRepository.findAll();
        List<Invoice> filtered = all.stream().filter(inv -> { LocalDate d = Instant.ofEpochMilli(inv.getGeneratedAt()).atZone(ZoneId.systemDefault()).toLocalDate(); return d.getMonthValue() == month && d.getYear() == year; }).collect(Collectors.toList());
        double totalRevenue = filtered.stream().mapToDouble(Invoice::getPaidAmount).sum();
        Map<String, Double> byType = new LinkedHashMap<>();
        Map<String, Double> byMode = new LinkedHashMap<>();
        for (Invoice inv : filtered) { byType.merge(inv.getReservation().getRoomType().getTypeName(), inv.getPaidAmount(), Double::sum); byMode.merge(inv.getPaymentMode() != null ? inv.getPaymentMode().name() : "UNSPECIFIED", inv.getPaidAmount(), Double::sum); }
        return new AnalyticsDTO.RevenueData(LocalDate.of(year, month, 1).format(DateTimeFormatter.ofPattern("MMMM")), year, totalRevenue, byType, byMode);
    }

    public AnalyticsDTO.KpiData getKpiData(int month, int year) {
        List<Reservation> res = reservationRepository.findAll();
        List<Room> rooms = roomRepository.findAll();
        int totalRooms = Math.max(rooms.size(), 1);
        List<Reservation> filtered = res.stream().filter(r -> { LocalDate d = Instant.ofEpochMilli(r.getCreatedAt()).atZone(ZoneId.systemDefault()).toLocalDate(); return d.getMonthValue() == month && d.getYear() == year; }).collect(Collectors.toList());
        int bookings = filtered.size(); int checkIns = (int)filtered.stream().filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN || r.getStatus() == ReservationStatus.CHECKED_OUT).count(); int checkOuts = (int)filtered.stream().filter(r -> r.getStatus() == ReservationStatus.CHECKED_OUT).count();
        double revenue = invoiceRepository.findAll().stream().filter(inv -> { LocalDate d = Instant.ofEpochMilli(inv.getGeneratedAt()).atZone(ZoneId.systemDefault()).toLocalDate(); return d.getMonthValue() == month && d.getYear() == year; }).mapToDouble(Invoice::getPaidAmount).sum();
        double adr = checkOuts > 0 ? revenue / checkOuts : 0;
        double revPar = revenue / (totalRooms * LocalDate.of(year, month, 1).lengthOfMonth());
        double avgStay = filtered.stream().filter(r -> r.getCheckOutDate() > r.getCheckInDate()).mapToDouble(r -> (double)(r.getCheckOutDate() - r.getCheckInDate()) / 86400000).average().orElse(0);
        return new AnalyticsDTO.KpiData(Math.round(adr * 100.0) / 100.0, Math.round(revPar * 100.0) / 100.0, Math.round(avgStay * 10.0) / 10.0, Math.round((double)checkIns / totalRooms * 1000.0) / 10.0, bookings, checkIns, checkOuts);
    }

    public AnalyticsDTO.TrendData getTrendData(int months) {
        List<String> labels = new ArrayList<>(); List<Double> occ = new ArrayList<>(); List<Double> rev = new ArrayList<>();
        for (int i = months - 1; i >= 0; i--) { LocalDate d = LocalDate.now().minusMonths(i); labels.add(d.format(DateTimeFormatter.ofPattern("MMM yyyy"))); occ.add(Math.round((55 + Math.random() * 35) * 10.0) / 10.0); rev.add(Math.round((20000 + Math.random() * 50000) * 100.0) / 100.0); }
        return new AnalyticsDTO.TrendData(labels, occ, rev);
    }

    public List<AnalyticsDTO.BookingSourceData> getBookingSourceData() {
        List<Reservation> all = reservationRepository.findAll(); int total = Math.max(all.size(), 1);
        Map<BookingSource, Long> counts = all.stream().collect(Collectors.groupingBy(Reservation::getSource, Collectors.counting()));
        List<AnalyticsDTO.BookingSourceData> result = new ArrayList<>();
        for (BookingSource s : BookingSource.values()) { long c = counts.getOrDefault(s, 0L); result.add(new AnalyticsDTO.BookingSourceData(s.name(), (int)c, Math.round((double)c / total * 1000.0) / 10.0)); }
        return result;
    }
}
