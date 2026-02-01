package com.velocity.core.enums;

import lombok.Getter;

/**
 * Notification types supported by the VeloCity system.
 * Defines different channels for sending notifications to users.
 */
@Getter
public enum NotificationType {
    
    EMAIL("Email", true, "Email notification"),
    SMS("SMS", true, "SMS notification"),
    PUSH("Push Notification", true, "Mobile push notification"),
    IN_APP("In-App", false, "In-app notification");
    
    private final String displayName;
    private final boolean requiresExternalService;
    private final String description;
    
    NotificationType(String displayName, boolean requiresExternalService, String description) {
        this.displayName = displayName;
        this.requiresExternalService = requiresExternalService;
        this.description = description;
    }
    
    /**
     * Check if this notification type requires an external service.
     * @return true if external service is needed (EMAIL, SMS, PUSH)
     */
    public boolean requiresExternalService() {
        return requiresExternalService;
    }
    
    /**
     * Check if this is an email notification.
     * @return true if type is EMAIL
     */
    public boolean isEmail() {
        return this == EMAIL;
    }
    
    /**
     * Check if this is an SMS notification.
     * @return true if type is SMS
     */
    public boolean isSms() {
        return this == SMS;
    }
    
    /**
     * Check if this is a push notification.
     * @return true if type is PUSH
     */
    public boolean isPush() {
        return this == PUSH;
    }
    
    /**
     * Check if this is an in-app notification.
     * @return true if type is IN_APP
     */
    public boolean isInApp() {
        return this == IN_APP;
    }
}
