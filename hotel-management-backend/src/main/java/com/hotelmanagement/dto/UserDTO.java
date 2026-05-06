package com.hotelmanagement.dto;

import com.hotelmanagement.entity.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String phone;
    private UserRole role;
    private String idProofType;
    private String idProofNumber;
    private String preferredCurrency;
    private boolean isActive;
    private Long createdAt;
}
