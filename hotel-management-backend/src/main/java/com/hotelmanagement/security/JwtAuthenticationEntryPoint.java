package com.hotelmanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelmanagement.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<?> errorResponse = new ApiResponse<>(
                false,
                "Unauthorized: " + authException.getMessage(),
                null
        );

        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
