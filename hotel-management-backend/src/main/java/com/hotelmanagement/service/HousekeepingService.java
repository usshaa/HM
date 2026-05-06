package com.hotelmanagement.service;

import com.hotelmanagement.dto.HousekeepingTaskDTO;
import com.hotelmanagement.dto.MaintenanceRequestDTO;
import com.hotelmanagement.entity.*;
import com.hotelmanagement.exception.BadRequestException;
import com.hotelmanagement.exception.ResourceNotFoundException;
import com.hotelmanagement.repository.HousekeepingTaskRepository;
import com.hotelmanagement.repository.MaintenanceRequestRepository;
import com.hotelmanagement.repository.RoomRepository;
import com.hotelmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HousekeepingService {

    private final HousekeepingTaskRepository taskRepository;
    private final MaintenanceRequestRepository maintenanceRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    @Transactional
    public HousekeepingTaskDTO createTask(HousekeepingTaskDTO dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        User staff = userRepository.findById(dto.getAssignedToId())
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        if (staff.getRole() != UserRole.HOUSEKEEPING) {
            throw new BadRequestException("Tasks can only be assigned to housekeeping staff");
        }

        HousekeepingTask task = new HousekeepingTask();
        task.setRoom(room);
        task.setAssignedTo(staff);
        task.setTaskType(dto.getTaskType() != null ? dto.getTaskType() : TaskType.DAILY_SERVICE);
        task.setStatus(TaskStatus.PENDING);
        task.setPriority(dto.getPriority() != null ? dto.getPriority() : Priority.MEDIUM);
        task.setScheduledDate(dto.getScheduledDate() != null ? dto.getScheduledDate() : System.currentTimeMillis());
        task.setNotes(dto.getNotes());

        HousekeepingTask saved = taskRepository.save(task);
        log.info("Housekeeping task created: {} for room {}", saved.getId(), room.getRoomNumber());
        return convertTaskToDTO(saved);
    }

    @Transactional
    public HousekeepingTaskDTO updateTaskStatus(Long taskId, TaskStatus newStatus) {
        HousekeepingTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        task.setStatus(newStatus);
        if (newStatus == TaskStatus.COMPLETED) {
            task.setCompletedAt(System.currentTimeMillis());
            // Update room status to CLEAN when cleaning task is completed
            if (task.getTaskType() == TaskType.CHECKOUT_CLEAN || task.getTaskType() == TaskType.DAILY_SERVICE || task.getTaskType() == TaskType.DEEP_CLEAN) {
                Room room = task.getRoom();
                room.setStatus(RoomStatus.CLEAN);
                room.setLastCleaned(System.currentTimeMillis());
                roomRepository.save(room);
            }
        }

        HousekeepingTask updated = taskRepository.save(task);
        log.info("Task {} status updated to {}", taskId, newStatus);
        return convertTaskToDTO(updated);
    }

    public Page<HousekeepingTaskDTO> getTasks(Long scheduledDate, Long assignedToId, Pageable pageable) {
        if (assignedToId != null) {
            return taskRepository.findByAssignedToId(assignedToId, pageable).map(this::convertTaskToDTO);
        }
        return taskRepository.findAll(pageable).map(this::convertTaskToDTO);
    }

    @Transactional
    public MaintenanceRequestDTO createMaintenanceRequest(MaintenanceRequestDTO dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room not found"));

        User reporter = userRepository.findById(dto.getReportedById())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MaintenanceRequest request = new MaintenanceRequest();
        request.setRoom(room);
        request.setReportedBy(reporter);
        request.setIssueDescription(dto.getIssueDescription());
        request.setPriority(dto.getPriority() != null ? dto.getPriority() : Priority.MEDIUM);
        request.setStatus(MaintenanceStatus.OPEN);

        // Set room to maintenance status
        room.setStatus(RoomStatus.MAINTENANCE);
        roomRepository.save(room);

        MaintenanceRequest saved = maintenanceRepository.save(request);
        log.info("Maintenance request created: {} for room {}", saved.getId(), room.getRoomNumber());
        return convertMaintenanceToDTO(saved);
    }

    @Transactional
    public MaintenanceRequestDTO updateMaintenanceStatus(Long requestId, MaintenanceStatus newStatus) {
        MaintenanceRequest request = maintenanceRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance request not found"));

        request.setStatus(newStatus);
        if (newStatus == MaintenanceStatus.RESOLVED || newStatus == MaintenanceStatus.CLOSED) {
            request.setResolvedAt(System.currentTimeMillis());
            // Restore room to AVAILABLE
            Room room = request.getRoom();
            room.setStatus(RoomStatus.AVAILABLE);
            roomRepository.save(room);
        }

        MaintenanceRequest updated = maintenanceRepository.save(request);
        log.info("Maintenance request {} status updated to {}", requestId, newStatus);
        return convertMaintenanceToDTO(updated);
    }

    public Page<MaintenanceRequestDTO> getMaintenanceRequests(MaintenanceStatus status, Pageable pageable) {
        if (status != null) {
            return maintenanceRepository.findByStatus(status, pageable).map(this::convertMaintenanceToDTO);
        }
        return maintenanceRepository.findAll(pageable).map(this::convertMaintenanceToDTO);
    }

    private HousekeepingTaskDTO convertTaskToDTO(HousekeepingTask task) {
        return new HousekeepingTaskDTO(
                task.getId(),
                task.getRoom().getId(),
                task.getRoom().getRoomNumber(),
                task.getAssignedTo().getId(),
                task.getAssignedTo().getName(),
                task.getTaskType(),
                task.getStatus(),
                task.getPriority(),
                task.getScheduledDate(),
                task.getCompletedAt(),
                task.getNotes()
        );
    }

    private MaintenanceRequestDTO convertMaintenanceToDTO(MaintenanceRequest req) {
        return new MaintenanceRequestDTO(
                req.getId(),
                req.getRoom().getId(),
                req.getRoom().getRoomNumber(),
                req.getReportedBy().getId(),
                req.getReportedBy().getName(),
                req.getIssueDescription(),
                req.getPriority(),
                req.getStatus(),
                req.getCreatedAt(),
                req.getResolvedAt()
        );
    }
}
