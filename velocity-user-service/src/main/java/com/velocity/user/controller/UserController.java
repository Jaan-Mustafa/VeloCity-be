package com.velocity.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.velocity.core.dto.ApiResponse;
import com.velocity.user.model.dto.ChangePasswordRequest;
import com.velocity.user.model.dto.UpdateProfileRequest;
import com.velocity.user.model.dto.UserDto;
import com.velocity.user.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for user profile management
 */
@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "User Management", description = "User profile management endpoints")
public class UserController {

    private final UserService userService;

    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    @Operation(summary = "Get profile", description = "Get current user's profile")
    public ResponseEntity<ApiResponse<UserDto>> getProfile() {
        Long userId = getCurrentUserId();
        log.info("Get profile request for user ID: {}", userId);
        
        UserDto user = userService.getUserById(userId);
        
        return ResponseEntity.ok(ApiResponse.success(user, "Profile retrieved successfully"));
    }

    /**
     * Get user by ID (admin or self)
     */
    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID", description = "Get user details by ID")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long userId) {
        log.info("Get user request for ID: {}", userId);
        
        UserDto user = userService.getUserById(userId);
        
        return ResponseEntity.ok(ApiResponse.success(user, "User retrieved successfully"));
    }

    /**
     * Update user profile
     */
    @PutMapping("/profile")
    @Operation(summary = "Update profile", description = "Update current user's profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = getCurrentUserId();
        log.info("Update profile request for user ID: {}", userId);
        
        UserDto user = userService.updateProfile(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success(user, "Profile updated successfully"));
    }

    /**
     * Change password
     */
    @PutMapping("/change-password")
    @Operation(summary = "Change password", description = "Change user password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = getCurrentUserId();
        log.info("Change password request for user ID: {}", userId);
        
        userService.changePassword(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    /**
     * Deactivate account
     */
    @DeleteMapping("/profile")
    @Operation(summary = "Deactivate account", description = "Deactivate current user's account")
    public ResponseEntity<ApiResponse<Void>> deactivateAccount() {
        Long userId = getCurrentUserId();
        log.info("Deactivate account request for user ID: {}", userId);
        
        userService.deactivateUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Account deactivated successfully"));
    }

    /**
     * Verify email (simplified - in production would use token)
     */
    @PostMapping("/verify/{userId}")
    @Operation(summary = "Verify user", description = "Verify user email (admin only)")
    public ResponseEntity<ApiResponse<Void>> verifyUser(@PathVariable Long userId) {
        log.info("Verify user request for ID: {}", userId);
        
        userService.verifyUser(userId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "User verified successfully"));
    }

    /**
     * Helper method to get current user ID from security context
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }
}
