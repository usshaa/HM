package com.hotelmanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogDTO {
    private Long id;
    private Long userId;
    private String userName;
    private String action;
    private String entityType;
    private Long entityId;
    private String details;
    private Long timestamp;
}
