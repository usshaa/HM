package com.hotelmanagement.dto;

import com.hotelmanagement.entity.ReservationStatus;
import com.hotelmanagement.entity.BookingSource;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Long id;
    private Long guestId;
    private String guestName;
    private Long roomId;
    private String roomNumber;
    private Long roomTypeId;
    private String roomTypeName;
    private Long checkInDate;
    private Long checkOutDate;
    private Integer numAdults;
    private Integer numChildren;
    private ReservationStatus status;
    private BookingSource source;
    private String specialRequests;
    private Double totalAmount;
    private Boolean earlyCheckInRequested;
    private Boolean lateCheckOutRequested;
    private Long createdAt;
}
