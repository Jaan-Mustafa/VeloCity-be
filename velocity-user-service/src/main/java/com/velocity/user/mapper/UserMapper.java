package com.velocity.user.mapper;

import org.springframework.stereotype.Component;

import com.velocity.user.model.dto.UserDto;
import com.velocity.user.model.entity.User;

/**
 * Mapper for converting between User entity and UserDto
 */
@Component
public class UserMapper {

    /**
     * Convert User entity to UserDto
     */
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    /**
     * Update User entity from UserDto (for profile updates)
     */
    public void updateEntityFromDto(User user, UserDto dto) {
        if (dto.getName() != null) {
            user.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getProfileImageUrl() != null) {
            user.setProfileImageUrl(dto.getProfileImageUrl());
        }
    }
}
