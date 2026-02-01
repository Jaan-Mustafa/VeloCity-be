package com.velocity.core.constants;

import lombok.Getter;

/**
 * Centralized error codes for the VeloCity application.
 * Error codes are organized by category for easy identification and troubleshooting.
 */
@Getter
public enum ErrorCodes {
    
    // Validation Errors (1000-1999)
    VALIDATION_ERROR("1000", "Validation failed"),
    INVALID_EMAIL("1001", "Invalid email format"),
    INVALID_PHONE("1002", "Invalid phone number format"),
    INVALID_COORDINATES("1003", "Invalid latitude or longitude"),
    REQUIRED_FIELD_MISSING("1004", "Required field is missing"),
    INVALID_INPUT("1005", "Invalid input provided"),
    
    // Authentication & Authorization Errors (2000-2999)
    UNAUTHORIZED("2000", "Unauthorized access"),
    INVALID_CREDENTIALS("2001", "Invalid email/phone or password"),
    TOKEN_EXPIRED("2002", "Authentication token has expired"),
    TOKEN_INVALID("2003", "Invalid authentication token"),
    INSUFFICIENT_PERMISSIONS("2004", "Insufficient permissions for this operation"),
    ACCOUNT_LOCKED("2005", "Account has been locked"),
    ACCOUNT_NOT_VERIFIED("2006", "Account is not verified"),
    
    // Business Logic Errors (3000-3999)
    RESOURCE_NOT_FOUND("3000", "Requested resource not found"),
    USER_NOT_FOUND("3001", "User not found"),
    DRIVER_NOT_FOUND("3002", "Driver not found"),
    RIDE_NOT_FOUND("3003", "Ride not found"),
    WALLET_NOT_FOUND("3004", "Wallet not found"),
    
    DUPLICATE_RESOURCE("3010", "Resource already exists"),
    DUPLICATE_EMAIL("3011", "Email already registered"),
    DUPLICATE_PHONE("3012", "Phone number already registered"),
    DUPLICATE_LICENSE("3013", "License number already registered"),
    
    BUSINESS_RULE_VIOLATION("3020", "Business rule violation"),
    INSUFFICIENT_BALANCE("3021", "Insufficient wallet balance"),
    DRIVER_NOT_AVAILABLE("3022", "No drivers available in the area"),
    INVALID_RIDE_STATUS("3023", "Invalid ride status transition"),
    RIDE_ALREADY_ACCEPTED("3024", "Ride has already been accepted"),
    RIDE_CANNOT_BE_CANCELLED("3025", "Ride cannot be cancelled at this stage"),
    DRIVER_ALREADY_BUSY("3026", "Driver is already assigned to another ride"),
    
    // System Errors (4000-4999)
    INTERNAL_SERVER_ERROR("4000", "Internal server error occurred"),
    DATABASE_ERROR("4001", "Database operation failed"),
    EXTERNAL_SERVICE_ERROR("4002", "External service unavailable"),
    CACHE_ERROR("4003", "Cache operation failed"),
    NOTIFICATION_ERROR("4004", "Failed to send notification"),
    PAYMENT_PROCESSING_ERROR("4005", "Payment processing failed");
    
    private final String code;
    private final String message;
    
    ErrorCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }
    
    /**
     * Get the full error code with prefix.
     * @return formatted error code (e.g., "ERR-1001")
     */
    public String getFullCode() {
        return "ERR-" + code;
    }
}
