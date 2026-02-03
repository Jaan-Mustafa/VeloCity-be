package com.velocity.notification.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velocity.core.exception.ResourceNotFoundException;
import com.velocity.notification.mapper.NotificationMapper;
import com.velocity.notification.model.dto.EmailRequest;
import com.velocity.notification.model.dto.NotificationDto;
import com.velocity.notification.model.dto.NotificationResponse;
import com.velocity.notification.model.dto.SendNotificationRequest;
import com.velocity.notification.model.entity.Notification;
import com.velocity.notification.repository.NotificationRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for notification operations.
 * Handles sending and managing notifications across multiple channels.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final EmailService emailService;
    
    /**
     * Send a notification
     */
    @Transactional
    public NotificationResponse sendNotification(SendNotificationRequest request) {
        log.info("Sending notification to user: {}, type: {}", request.getUserId(), request.getType());
        
        // Create and save notification record
        Notification notification = new Notification();
        notification.setUserId(request.getUserId());
        notification.setType(request.getType());
        notification.setTitle(request.getTitle());
        notification.setMessage(request.getMessage());
        notification.setData(request.getData());
        notification.setIsRead(false);
        
        Notification savedNotification = notificationRepository.save(notification);
        
        // Send notification based on type
        try {
            switch (request.getType()) {
                case EMAIL:
                    if (request.getEmail() != null) {
                        EmailRequest emailRequest = EmailRequest.builder()
                                .to(request.getEmail())
                                .subject(request.getTitle())
                                .body(request.getMessage())
                                .isHtml(false)
                                .build();
                        emailService.sendEmail(emailRequest);
                    }
                    break;
                    
                case SMS:
                    // SMS implementation (dummy for now)
                    log.info("SMS notification would be sent to: {}", request.getPhone());
                    break;
                    
                case PUSH:
                    // Push notification implementation (dummy for now)
                    log.info("Push notification would be sent to user: {}", request.getUserId());
                    break;
                    
                case IN_APP:
                    // In-app notification is already saved in database
                    log.info("In-app notification saved for user: {}", request.getUserId());
                    break;
            }
            
            // Mark as sent
            savedNotification.markAsSent();
            notificationRepository.save(savedNotification);
            
            log.info("Notification sent successfully. ID: {}", savedNotification.getId());
            
            return NotificationResponse.builder()
                    .success(true)
                    .message("Notification sent successfully")
                    .notificationId(savedNotification.getId())
                    .build();
                    
        } catch (Exception e) {
            log.error("Failed to send notification: {}", e.getMessage(), e);
            return NotificationResponse.builder()
                    .success(false)
                    .message("Failed to send notification: " + e.getMessage())
                    .notificationId(savedNotification.getId())
                    .build();
        }
    }
    
    /**
     * Get notifications for a user
     */
    @Transactional(readOnly = true)
    public Page<NotificationDto> getUserNotifications(Long userId, Pageable pageable) {
        log.debug("Fetching notifications for user: {}", userId);
        
        Page<Notification> notifications = notificationRepository.findByUserId(userId, pageable);
        
        return notifications.map(notificationMapper::toDto);
    }
    
    /**
     * Get unread notifications for a user
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getUnreadNotifications(Long userId) {
        log.debug("Fetching unread notifications for user: {}", userId);
        
        Page<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalse(
                userId, Pageable.unpaged());
        
        return notifications.stream()
                .map(notificationMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Get unread notification count
     */
    @Transactional(readOnly = true)
    public Long getUnreadCount(Long userId) {
        log.debug("Counting unread notifications for user: {}", userId);
        
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
    
    /**
     * Mark notification as read
     */
    @Transactional
    public NotificationDto markAsRead(Long notificationId) {
        log.info("Marking notification as read: {}", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        
        notification.markAsRead();
        notificationRepository.save(notification);
        
        return notificationMapper.toDto(notification);
    }
    
    /**
     * Mark all notifications as read for a user
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        log.info("Marking all notifications as read for user: {}", userId);
        
        int updatedCount = notificationRepository.markAllAsReadForUser(userId, LocalDateTime.now());
        
        log.info("Marked {} notifications as read for user: {}", updatedCount, userId);
    }
    
    /**
     * Delete notification
     */
    @Transactional
    public void deleteNotification(Long notificationId) {
        log.info("Deleting notification: {}", notificationId);
        
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("Notification not found: " + notificationId);
        }
        
        notificationRepository.deleteById(notificationId);
    }
    
    /**
     * Delete old notifications (cleanup job)
     */
    @Transactional
    public void deleteOldNotifications(int daysOld) {
        log.info("Deleting notifications older than {} days", daysOld);
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        int deletedCount = notificationRepository.deleteOldReadNotifications(cutoffDate);
        
        log.info("Deleted {} old notifications", deletedCount);
    }
    
    /**
     * Get notification by ID
     */
    @Transactional(readOnly = true)
    public NotificationDto getNotification(Long notificationId) {
        log.debug("Fetching notification: {}", notificationId);
        
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found: " + notificationId));
        
        return notificationMapper.toDto(notification);
    }
}
