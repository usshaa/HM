package com.hotelmanagement.dto;

import com.hotelmanagement.entity.MaintenanceStatus;
import com.hotelmanagement.entity.Priority;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceRequestDTO {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private Long reportedById;
    private String reportedByName;
    private String issueDescription;
    private Priority priority;
    private MaintenanceStatus status;
    private Long createdAt;
    private Long resolvedAt;
}
