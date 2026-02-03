package com.velocity.user.service;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.velocity.core.exception.UnauthorizedException;
import com.velocity.user.mapper.UserMapper;
import com.velocity.user.model.dto.LoginRequest;
import com.velocity.user.model.dto.LoginResponse;
import com.velocity.user.model.dto.RefreshTokenRequest;
import com.velocity.user.model.dto.UserDto;
import com.velocity.user.model.entity.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for authentication operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 7;

    /**
     * Authenticate user and generate tokens
     */
    public LoginResponse login(LoginRequest request) {
        log.info("Login attempt for: {}", request.getEmailOrPhone());

        // Find user by email or phone
        User user = findUserByEmailOrPhone(request.getEmailOrPhone());

        // Check if user is active
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is deactivated. Please contact support.");
        }

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            log.warn("Failed login attempt for: {}", request.getEmailOrPhone());
            throw new UnauthorizedException("Invalid credentials");
        }

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        String refreshToken = jwtService.generateRefreshToken(
                user.getId(),
                user.getEmail()
        );

        // Store refresh token in Redis
        storeRefreshToken(user.getId(), refreshToken);

        UserDto userDto = userMapper.toDto(user);

        log.info("User logged in successfully: {}", user.getEmail());

        return LoginResponse.of(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenExpirationInSeconds(),
                userDto
        );
    }

    /**
     * Refresh access token using refresh token
     */
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        // Validate refresh token
        if (!jwtService.validateRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }

        // Extract user information
        Long userId = jwtService.extractUserId(refreshToken);
        String email = jwtService.extractUsername(refreshToken);

        // Verify refresh token exists in Redis
        String storedToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new UnauthorizedException("Refresh token not found or has been revoked");
        }

        // Get user details
        UserDto userDto = userService.getUserById(userId);

        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(
                userId,
                email,
                userDto.getRole().name()
        );

        log.info("Access token refreshed for user: {}", email);

        return LoginResponse.of(
                newAccessToken,
                refreshToken, // Return same refresh token
                jwtService.getAccessTokenExpirationInSeconds(),
                userDto
        );
    }

    /**
     * Logout user by invalidating refresh token
     */
    public void logout(Long userId) {
        log.info("Logging out user ID: {}", userId);

        // Remove refresh token from Redis
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);

        log.info("User logged out successfully: {}", userId);
    }

    /**
     * Find user by email or phone
     */
    private User findUserByEmailOrPhone(String emailOrPhone) {
        // Check if it's an email (contains @)
        if (emailOrPhone.contains("@")) {
            return userService.getUserEntityByEmail(emailOrPhone);
        } else {
            // Assume it's a phone number
            return userService.getUserEntityByPhone(emailOrPhone);
        }
    }

    /**
     * Store refresh token in Redis
     */
    private void storeRefreshToken(Long userId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                REFRESH_TOKEN_VALIDITY_DAYS,
                TimeUnit.DAYS
        );
    }

    /**
     * Validate if user has valid session
     */
    public boolean hasValidSession(Long userId) {
        String key = REFRESH_TOKEN_PREFIX + userId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Revoke all sessions for a user
     */
    public void revokeAllSessions(Long userId) {
        log.info("Revoking all sessions for user ID: {}", userId);
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }
}
