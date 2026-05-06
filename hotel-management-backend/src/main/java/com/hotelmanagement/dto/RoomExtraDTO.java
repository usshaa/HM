package com.hotelmanagement.dto;

import com.hotelmanagement.entity.ExtraType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomExtraDTO {
    private Long id;
    private Long reservationId;
    private ExtraType extraType;
    private String description;
    private Double amount;
    private Long chargeDate;
    private Long addedById;
    private String addedByName;
}
