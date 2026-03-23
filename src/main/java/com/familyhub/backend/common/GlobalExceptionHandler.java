package com.familyhub.backend.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<Void>> handleAppException(AppException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getCode())
                .body(new ApiResponse<>(ex.getCode(), ex.getMessage(), null, System.currentTimeMillis(), traceId(request)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(400, message, null, System.currentTimeMillis(), traceId(request)));
    }

    @ExceptionHandler({ConstraintViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(new ApiResponse<>(400, ex.getMessage(), null, System.currentTimeMillis(), traceId(request)));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        return ResponseEntity.status(403)
                .body(new ApiResponse<>(403, "权限不足", null, System.currentTimeMillis(), traceId(request)));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneric(Exception ex, HttpServletRequest request) {
        return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(500, ex.getMessage(), null, System.currentTimeMillis(), traceId(request)));
    }

    private String traceId(HttpServletRequest request) {
        Object value = request.getAttribute("traceId");
        return value == null ? "N/A" : value.toString();
    }
}
