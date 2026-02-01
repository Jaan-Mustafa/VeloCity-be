package com.velocity.core.exception;

import lombok.Getter;

/**
 * Exception thrown when authentication or authorization fails.
 * Used for JWT validation failures, missing tokens, or insufficient permissions.
 * 
 * @author VeloCity Team
 * @since 1.0.0
 */
@Getter
public class UnauthorizedException extends RuntimeException {
    
    private final UnauthorizedReason reason;
    
    /**
     * Constructor with simple message.
     *
     * @param message the unauthorized error message
     */
    public UnauthorizedException(String message) {
        super(message);
        this.reason = UnauthorizedReason.GENERIC;
    }
    
    /**
     * Constructor with specific reason.
     *
     * @param reason the specific reason for unauthorized access
     */
    public UnauthorizedException(UnauthorizedReason reason) {
        super(reason.getMessage());
        this.reason = reason;
    }
    
    /**
     * Constructor with custom message and reason.
     *
     * @param message custom error message
     * @param reason the specific reason for unauthorized access
     */
    public UnauthorizedException(String message, UnauthorizedReason reason) {
        super(message);
        this.reason = reason;
    }
    
    /**
     * Enum defining specific reasons for unauthorized access.
     */
    @Getter
    public enum UnauthorizedReason {
        GENERIC("Unauthorized access"),
        INVALID_TOKEN("Invalid or expired authentication token"),
        MISSING_TOKEN("Authentication token is missing"),
        INSUFFICIENT_PERMISSIONS("Insufficient permissions to access this resource"),
        ACCOUNT_DISABLED("Account has been disabled"),
        ACCOUNT_LOCKED("Account has been locked");
        
        private final String message;
        
        UnauthorizedReason(String message) {
            this.message = message;
        }
    }
}
