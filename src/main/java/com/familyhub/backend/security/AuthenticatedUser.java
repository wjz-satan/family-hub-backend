package com.familyhub.backend.security;

public record AuthenticatedUser(Long userId, String phone, String nickname) {
}
