package com.velocity.notification.model.dto;

import java.time.LocalDateTime;
import java.util.Map;

import com.velocity.core.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Notification information.
 * Used for API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
    
    private Long id;
    private Long userId;
    private NotificationType type;
    private String title;
    private String message;
    private Map<String, Object> data;
    private Boolean isRead;
    private LocalDateTime sentAt;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
}
