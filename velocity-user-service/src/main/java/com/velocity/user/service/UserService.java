package com.velocity.user.service;

import com.velocity.core.enums.UserRole;
import com.velocity.core.exception.ResourceNotFoundException;
import com.velocity.core.exception.ValidationException;
import com.velocity.user.mapper.UserMapper;
import com.velocity.user.model.dto.ChangePasswordRequest;
import com.velocity.user.model.dto.RegisterRequest;
import com.velocity.user.model.dto.UpdateProfileRequest;
import com.velocity.user.model.dto.UserDto;
import com.velocity.user.model.entity.User;
import com.velocity.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for user management operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     */
    @Transactional
    public UserDto registerUser(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ValidationException("Email already exists");
        }

        // Validate phone uniqueness
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new ValidationException("Phone number already exists");
        }

        // Parse and validate role
        UserRole role;
        try {
            role = UserRole.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid role. Must be RIDER, DRIVER, or ADMIN");
        }

        // Create user entity
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail().toLowerCase())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .isActive(true)
                .isVerified(false)
                .profileImageUrl(request.getProfileImageUrl())
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return userMapper.toDto(savedUser);
    }

    /**
     * Get user by ID
     */
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return userMapper.toDto(user);
    }

    /**
     * Get user by email
     */
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toDto(user);
    }

    /**
     * Get user by phone
     */
    public UserDto getUserByPhone(String phone) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with phone: " + phone));
        return userMapper.toDto(user);
    }

    /**
     * Get user entity by email (for authentication)
     */
    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    /**
     * Get user entity by phone (for authentication)
     */
    public User getUserEntityByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with phone: " + phone));
    }

    /**
     * Update user profile
     */
    @Transactional
    public UserDto updateProfile(Long userId, UpdateProfileRequest request) {
        log.info("Updating profile for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Update name if provided
        if (request.getName() != null && !request.getName().isBlank()) {
            user.setName(request.getName());
        }

        // Update email if provided and different
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().toLowerCase();
            if (!newEmail.equals(user.getEmail())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new ValidationException("Email already exists");
                }
                user.setEmail(newEmail);
                user.setIsVerified(false); // Require re-verification
            }
        }

        // Update phone if provided and different
        if (request.getPhone() != null && !request.getPhone().isBlank()) {
            if (!request.getPhone().equals(user.getPhone())) {
                if (userRepository.existsByPhone(request.getPhone())) {
                    throw new ValidationException("Phone number already exists");
                }
                user.setPhone(request.getPhone());
            }
        }

        // Update profile image if provided
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", userId);

        return userMapper.toDto(updatedUser);
    }

    /**
     * Change user password
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        log.info("Changing password for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect");
        }

        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully for user ID: {}", userId);
    }

    /**
     * Deactivate user account
     */
    @Transactional
    public void deactivateUser(Long userId) {
        log.info("Deactivating user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setIsActive(false);
        userRepository.save(user);

        log.info("User deactivated successfully: {}", userId);
    }

    /**
     * Activate user account
     */
    @Transactional
    public void activateUser(Long userId) {
        log.info("Activating user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setIsActive(true);
        userRepository.save(user);

        log.info("User activated successfully: {}", userId);
    }

    /**
     * Verify user email
     */
    @Transactional
    public void verifyUser(Long userId) {
        log.info("Verifying user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        user.setIsVerified(true);
        userRepository.save(user);

        log.info("User verified successfully: {}", userId);
    }

    /**
     * Check if user exists by email
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email.toLowerCase());
    }

    /**
     * Check if user exists by phone
     */
    public boolean existsByPhone(String phone) {
        return userRepository.existsByPhone(phone);
    }
}
