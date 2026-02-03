package com.velocity.notification.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.velocity.core.dto.ApiResponse;
import com.velocity.notification.model.dto.NotificationDto;
import com.velocity.notification.model.dto.NotificationResponse;
import com.velocity.notification.model.dto.SendNotificationRequest;
import com.velocity.notification.service.NotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for notification operations.
 * Provides endpoints for sending and managing notifications.
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Notification Management", description = "APIs for notification operations")
public class NotificationController {
    
    private final NotificationService notificationService;
    
    /**
     * Send a notification
     */
    @PostMapping("/send")
    @Operation(summary = "Send notification", description = "Send a notification to a user")
    public ResponseEntity<ApiResponse<NotificationResponse>> sendNotification(
            @Valid @RequestBody SendNotificationRequest request) {
        log.info("Request to send notification to user: {}, type: {}", request.getUserId(), request.getType());
        
        NotificationResponse response = notificationService.sendNotification(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Notification sent successfully"));
    }
    
    /**
     * Get notifications for a user
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get notifications", description = "Get all notifications for a user")
    public ResponseEntity<ApiResponse<Page<NotificationDto>>> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Request to get notifications for user: {}, page: {}, size: {}", userId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<NotificationDto> notifications = notificationService.getUserNotifications(userId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(notifications, "Notifications retrieved successfully"));
    }
    
    /**
     * Get unread notifications for a user
     */
    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "Get unread notifications", description = "Get unread notifications for a user")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getUnreadNotifications(@PathVariable Long userId) {
        log.info("Request to get unread notifications for user: {}", userId);
        
        List<NotificationDto> notifications = notificationService.getUnreadNotifications(userId);
        
        return ResponseEntity.ok(ApiResponse.success(notifications, "Unread notifications retrieved successfully"));
    }
    
    /**
     * Get unread notification count
     */
    @GetMapping("/user/{userId}/unread-count")
    @Operation(summary = "Get unread count", description = "Get count of unread notifications")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(@PathVariable Long userId) {
        log.info("Request to get unread count for user: {}", userId);
        
        Long count = notificationService.getUnreadCount(userId);
        
        return ResponseEntity.ok(ApiResponse.success(count, "Unread count retrieved successfully"));
    }
    
    /**
     * Mark notification as read
     */
    @PutMapping("/{notificationId}/read")
    @Operation(summary = "Mark as read", description = "Mark a notification as read")
    public ResponseEntity<ApiResponse<NotificationDto>> markAsRead(@PathVariable Long notificationId) {
        log.info("Request to mark notification as read: {}", notificationId);
        
        NotificationDto notification = notificationService.markAsRead(notificationId);
        
        return ResponseEntity.ok(ApiResponse.success(notification, "Notification marked as read"));
    }
    
    /**
     * Mark all notifications as read for a user
     */
    @PutMapping("/user/{userId}/read-all")
    @Operation(summary = "Mark all as read", description = "Mark all notifications as read for a user")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(@PathVariable Long userId) {
        log.info("Request to mark all notifications as read for user: {}", userId);
        
        notificationService.markAllAsRead(userId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "All notifications marked as read"));
    }
    
    /**
     * Delete a notification
     */
    @DeleteMapping("/{notificationId}")
    @Operation(summary = "Delete notification", description = "Delete a notification")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long notificationId) {
        log.info("Request to delete notification: {}", notificationId);
        
        notificationService.deleteNotification(notificationId);
        
        return ResponseEntity.ok(ApiResponse.success(null, "Notification deleted successfully"));
    }
    
    /**
     * Get notification by ID
     */
    @GetMapping("/{notificationId}")
    @Operation(summary = "Get notification", description = "Get notification details by ID")
    public ResponseEntity<ApiResponse<NotificationDto>> getNotification(@PathVariable Long notificationId) {
        log.info("Request to get notification: {}", notificationId);
        
        NotificationDto notification = notificationService.getNotification(notificationId);
        
        return ResponseEntity.ok(ApiResponse.success(notification, "Notification retrieved successfully"));
    }
}
