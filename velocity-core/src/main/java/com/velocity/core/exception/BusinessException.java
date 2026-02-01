package com.velocity.core.exception;

import com.velocity.core.constants.ErrorCodes;

import lombok.Getter;

/**
 * Exception thrown when business logic rules are violated.
 * This is the base exception for all business-related errors.
 * 
 * Examples:
 * - Driver cannot accept ride (already on another ride)
 * - Ride cannot be cancelled (already in progress)
 * - Payment cannot be processed (insufficient balance)
 * 
 * @author VeloCity Team
 * @since 1.0.0
 */
@Getter
public class BusinessException extends RuntimeException {
    
    private final ErrorCodes errorCode;
    
    /**
     * Constructor with message and error code.
     *
     * @param message the business error message
     * @param errorCode the specific error code
     */
    public BusinessException(String message, ErrorCodes errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    /**
     * Constructor with message, error code, and cause.
     *
     * @param message the business error message
     * @param errorCode the specific error code
     * @param cause the underlying cause
     */
    public BusinessException(String message, ErrorCodes errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    /**
     * Constructor with just message (uses generic business error code).
     *
     * @param message the business error message
     */
    public BusinessException(String message) {
        super(message);
        this.errorCode = ErrorCodes.BUSINESS_RULE_VIOLATION;
    }
    
    /**
     * Get the full error code string.
     *
     * @return formatted error code (e.g., "VEL-3001")
     */
    public String getFullErrorCode() {
        return errorCode.getFullCode();
    }
}
