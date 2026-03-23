package com.familyhub.backend.service;

import com.familyhub.backend.common.AppException;
import com.familyhub.backend.dto.AuthDtos;
import com.familyhub.backend.entity.User;
import com.familyhub.backend.repository.UserRepository;
import com.familyhub.backend.security.AuthenticatedUser;
import com.familyhub.backend.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthDtos.TokenResponse login(AuthDtos.LoginRequest request) {
        User user = userRepository.findByPhone(request.phone())
                .orElseThrow(() -> new AppException(401, "账号或密码错误"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AppException(401, "账号或密码错误");
        }
        return issueTokens(user);
    }

    public AuthDtos.TokenResponse refresh(AuthDtos.RefreshTokenRequest request) {
        Claims claims = jwtService.parse(request.refreshToken());
        if (!jwtService.isRefreshToken(request.refreshToken())) {
            throw new AppException(401, "refresh token 无效");
        }
        User user = userRepository.findById(Long.valueOf(claims.getSubject()))
                .orElseThrow(() -> new AppException(401, "用户不存在"));
        return issueTokens(user);
    }

    public void resetPassword(AuthDtos.ResetPasswordRequest request) {
        User user = userRepository.findByPhone(request.phone())
                .orElseThrow(() -> new AppException(404, "用户不存在"));
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private AuthDtos.TokenResponse issueTokens(User user) {
        AuthenticatedUser authenticatedUser = new AuthenticatedUser(user.getId(), user.getPhone(), user.getNickname());
        return new AuthDtos.TokenResponse(
                jwtService.generateAccessToken(authenticatedUser),
                jwtService.generateRefreshToken(authenticatedUser),
                jwtService.getAccessTokenTtlSeconds(),
                new AuthDtos.UserProfile(user.getId(), user.getPhone(), user.getNickname(), user.getAvatarUrl())
        );
    }
}
