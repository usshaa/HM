package com.hotelmanagement.dto;

import com.hotelmanagement.entity.RoomStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDTO {
    private Long id;
    private String roomNumber;
    private Integer floor;
    private Long roomTypeId;
    private RoomType roomType;
    private RoomStatus status;
    private boolean isActive;
    private Long lastCleaned;
    private String notes;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomType {
        private Long id;
        private String typeName;
        private Double basePrice;
    }
}
