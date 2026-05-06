package com.hotelmanagement.dto;

import com.hotelmanagement.entity.Priority;
import com.hotelmanagement.entity.TaskStatus;
import com.hotelmanagement.entity.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HousekeepingTaskDTO {
    private Long id;
    private Long roomId;
    private String roomNumber;
    private Long assignedToId;
    private String assignedToName;
    private TaskType taskType;
    private TaskStatus status;
    private Priority priority;
    private Long scheduledDate;
    private Long completedAt;
    private String notes;
}
