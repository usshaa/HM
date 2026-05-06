package com.hotelmanagement.exception;

import com.hotelmanagement.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.error("Resource not found: {}", ex.getMessage());
        ApiResponse<?> response = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequestException(BadRequestException ex) {
        log.error("Bad request: {}", ex.getMessage());
        ApiResponse<?> response = new ApiResponse<>(false, ex.getMessage(), null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.add(error.getField() + ": " + error.getDefaultMessage())
        );
        ApiResponse<?> response = new ApiResponse<>(false, "Validation failed", null, errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGlobalException(Exception ex) {
        log.error("Unexpected error", ex);
        ApiResponse<?> response = new ApiResponse<>(false, "Internal server error", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
