package com.hotelmanagement.dto;

import com.hotelmanagement.entity.BedType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeDTO {
    private Long id;
    private String typeName;
    private String description;
    private Double basePrice;
    private Integer maxOccupancy;
    private BedType bedType;
    private String amenities;
    private String photoUrl;
    private boolean isActive;
}
