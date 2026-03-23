package com.familyhub.backend.controller;

import com.familyhub.backend.common.ApiResponse;
import com.familyhub.backend.dto.AuthDtos;
import com.familyhub.backend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController extends BaseController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<AuthDtos.TokenResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request, HttpServletRequest servletRequest) {
        return ApiResponse.success(authService.login(request), traceId(servletRequest));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthDtos.TokenResponse> refresh(@Valid @RequestBody AuthDtos.RefreshTokenRequest request, HttpServletRequest servletRequest) {
        return ApiResponse.success(authService.refresh(request), traceId(servletRequest));
    }

    @PostMapping("/password/reset")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody AuthDtos.ResetPasswordRequest request, HttpServletRequest servletRequest) {
        authService.resetPassword(request);
        return ApiResponse.success(null, traceId(servletRequest));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest servletRequest) {
        return ApiResponse.success(null, traceId(servletRequest));
    }

    private String traceId(HttpServletRequest request) {
        return String.valueOf(request.getAttribute("traceId"));
    }
}
