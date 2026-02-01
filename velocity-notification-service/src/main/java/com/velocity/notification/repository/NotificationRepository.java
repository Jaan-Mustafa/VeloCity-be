package com.velocity.notification.repository;

import com.velocity.core.enums.NotificationType;
import com.velocity.notification.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Notification entity.
 * Provides CRUD operations and custom queries for notification management.
 * 
 * Following rules.md:
 * - Extends JpaRepository for standard operations
 * - Paginated queries for notification history
 * - Bulk operations for marking as read
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find notifications by user ID with pagination
     * @param userId User ID
     * @param pageable Pagination parameters
     * @return Page of notifications
     */
    Page<Notification> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Find unread notifications by user ID
     * @param userId User ID
     * @param pageable Pagination parameters
     * @return Page of unread notifications
     */
    Page<Notification> findByUserIdAndIsReadFalse(Long userId, Pageable pageable);
    
    /**
     * Find notifications by user and type
     * @param userId User ID
     * @param type Notification type
     * @param pageable Pagination parameters
     * @return Page of notifications
     */
    Page<Notification> findByUserIdAndType(Long userId, NotificationType type, Pageable pageable);
    
    /**
     * Count unread notifications for user
     * @param userId User ID
     * @return Count of unread notifications
     */
    long countByUserIdAndIsReadFalse(Long userId);
    
    /**
     * Find recent notifications for user
     * @param userId User ID
     * @param limit Number of notifications to return
     * @return List of recent notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.userId = :userId ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("userId") Long userId, @Param("limit") int limit);
    
    /**
     * Mark all notifications as read for user
     * @param userId User ID
     * @param readAt Timestamp when marked as read
     * @return Number of notifications updated
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.userId = :userId AND n.isRead = false")
    int markAllAsReadForUser(@Param("userId") Long userId, @Param("readAt") LocalDateTime readAt);
    
    /**
     * Delete old notifications (older than specified date)
     * @param cutoffDate Cutoff date
     * @return Number of notifications deleted
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.createdAt < :cutoffDate AND n.isRead = true")
    int deleteOldReadNotifications(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find unsent notifications
     * @return List of unsent notifications
     */
    @Query("SELECT n FROM Notification n WHERE n.sentAt IS NULL ORDER BY n.createdAt ASC")
    List<Notification> findUnsentNotifications();
    
    /**
     * Count notifications by type
     * @param type Notification type
     * @return Count of notifications
     */
    long countByType(NotificationType type);
}
