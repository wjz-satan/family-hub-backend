package com.familyhub.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public final class AuthDtos {
    private AuthDtos() {
    }

    public record LoginRequest(
            @Pattern(regexp = "^\\d{11}$", message = "手机号格式不正确") String phone,
            @NotBlank(message = "密码不能为空") String password
    ) {
    }

    public record RefreshTokenRequest(@NotBlank(message = "refreshToken 不能为空") String refreshToken) {
    }

    public record ResetPasswordRequest(
            @Pattern(regexp = "^\\d{11}$", message = "手机号格式不正确") String phone,
            @Size(min = 8, message = "密码长度至少 8 位") String newPassword
    ) {
    }

    public record TokenResponse(
            String accessToken,
            String refreshToken,
            long expiresIn,
            UserProfile profile
    ) {
    }

    public record UserProfile(Long userId, String phone, String nickname, String avatarUrl) {
    }
}
