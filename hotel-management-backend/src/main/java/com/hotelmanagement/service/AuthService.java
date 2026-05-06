package com.hotelmanagement.service;

import com.hotelmanagement.dto.LoginRequest;
import com.hotelmanagement.dto.LoginResponse;
import com.hotelmanagement.dto.UserDTO;
import com.hotelmanagement.entity.User;
import com.hotelmanagement.entity.UserRole;
import com.hotelmanagement.exception.BadRequestException;
import com.hotelmanagement.exception.ResourceNotFoundException;
import com.hotelmanagement.repository.UserRepository;
import com.hotelmanagement.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserDTO registerGuest(UserDTO request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode("DefaultPassword123!"));
        user.setPhone(request.getPhone());
        user.setRole(UserRole.GUEST);
        user.setActive(true);
        user.setFirstLoginPasswordChangeRequired(true);
        user.setPreferredCurrency("AED");

        User savedUser = userRepository.save(user);
        log.info("Guest registered: {}", savedUser.getEmail());

        return convertToDTO(savedUser);
    }

    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));

            String accessToken = tokenProvider.generateAccessToken(user);
            String refreshToken = tokenProvider.generateRefreshToken(user);

            log.info("User logged in: {}", user.getEmail());

            return new LoginResponse(
                    accessToken,
                    refreshToken,
                    convertToDTO(user),
                    "Bearer"
            );
        } catch (Exception e) {
            log.error("Login failed for user", e);
            throw new BadRequestException("Invalid email or password");
        }
    }

    public String refreshToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new BadRequestException("Invalid refresh token");
        }

        String username = tokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return tokenProvider.generateAccessToken(user);
    }

    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setFirstLoginPasswordChangeRequired(false);
        userRepository.save(user);

        log.info("Password changed for user: {}", email);
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
}
