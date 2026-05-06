package com.hotelmanagement.service;

import com.hotelmanagement.dto.AuditLogDTO;
import com.hotelmanagement.dto.UserDTO;
import com.hotelmanagement.entity.AuditLog;
import com.hotelmanagement.entity.User;
import com.hotelmanagement.entity.UserRole;
import com.hotelmanagement.exception.BadRequestException;
import com.hotelmanagement.exception.ResourceNotFoundException;
import com.hotelmanagement.repository.AuditLogRepository;
import com.hotelmanagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final AuditLogRepository auditLogRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Transactional
    public UserDTO activateDeactivateUser(Long userId, boolean activate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setActive(activate);
        User updated = userRepository.save(user);
        log.info("User {} {} by admin", userId, activate ? "activated" : "deactivated");
        return convertToDTO(updated);
    }

    @Transactional
    public UserDTO createStaffAccount(UserDTO dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        if (dto.getRole() == UserRole.GUEST) {
            throw new BadRequestException("Cannot create a GUEST account through admin panel");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPassword(passwordEncoder.encode("Staff@123")); // Default staff password
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        user.setActive(true);
        user.setFirstLoginPasswordChangeRequired(true);
        user.setPreferredCurrency("AED");

        User saved = userRepository.save(user);
        log.info("Staff account created: {} with role {}", saved.getEmail(), saved.getRole());
        return convertToDTO(saved);
    }

    public Page<AuditLogDTO> getAuditLogs(Long fromDate, Long toDate, Pageable pageable) {
        if (fromDate != null && toDate != null) {
            return auditLogRepository.findByTimestampBetween(fromDate, toDate, pageable)
                    .map(this::convertAuditToDTO);
        }
        return auditLogRepository.findAll(pageable).map(this::convertAuditToDTO);
    }

    public void logAction(User user, String action, String entityType, Long entityId, String details) {
        AuditLog auditLog = new AuditLog();
        auditLog.setUser(user);
        auditLog.setAction(action);
        auditLog.setEntityType(entityType);
        auditLog.setEntityId(entityId);
        auditLog.setDetails(details);
        auditLogRepository.save(auditLog);
    }

    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhone(),
                user.getRole(),
                user.getIdProofType(),
                user.getIdProofNumber(),
                user.getPreferredCurrency(),
                user.isActive(),
                user.getCreatedAt()
        );
    }

    private AuditLogDTO convertAuditToDTO(AuditLog log) {
        return new AuditLogDTO(
                log.getId(),
                log.getUser() != null ? log.getUser().getId() : null,
                log.getUser() != null ? log.getUser().getName() : "System",
                log.getAction(),
                log.getEntityType(),
                log.getEntityId(),
                log.getDetails(),
                log.getTimestamp()
        );
    }
}
