package com.velocity.core.dto;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Standardized error response structure for all API errors.
 * Used by GlobalExceptionHandler to return consistent error information.
 * 
 * @author VeloCity Team
 * @version 1.0
 * @since 2026-02-01
 */
@Data
@Accessors(chain = true)
public class ErrorResponse {
    
    /**
     * Application-specific error code (e.g., VEL-1001)
     */
    private String errorCode;
    
    /**
     * Human-readable error message
     */
    private String message;
    
    /**
     * The API path where the error occurred
     */
    private String path;
    
    /**
     * Timestamp when the error occurred
     */
    private LocalDateTime timestamp;
    
    /**
     * Field-level validation errors (field name -> error message)
     * Only populated for validation errors
     */
    private Map<String, String> fieldErrors;
    
    /**
     * Creates an error response with all fields
     * 
     * @param errorCode Application error code
     * @param message Error message
     * @param path Request path
     * @return ErrorResponse instance
     */
    public static ErrorResponse of(String errorCode, String message, String path) {
        return new ErrorResponse()
                .setErrorCode(errorCode)
                .setMessage(message)
                .setPath(path)
                .setTimestamp(LocalDateTime.now());
    }
    
    /**
     * Creates an error response without path
     * 
     * @param errorCode Application error code
     * @param message Error message
     * @return ErrorResponse instance
     */
    public static ErrorResponse of(String errorCode, String message) {
        return new ErrorResponse()
                .setErrorCode(errorCode)
                .setMessage(message)
                .setTimestamp(LocalDateTime.now());
    }
    
    /**
     * Creates an error response with field errors
     * 
     * @param errorCode Application error code
     * @param message Error message
     * @param path Request path
     * @param fieldErrors Map of field-level errors
     * @return ErrorResponse instance
     */
    public static ErrorResponse withFieldErrors(
            String errorCode, 
            String message, 
            String path,
            Map<String, String> fieldErrors) {
        return new ErrorResponse()
                .setErrorCode(errorCode)
                .setMessage(message)
                .setPath(path)
                .setFieldErrors(fieldErrors)
                .setTimestamp(LocalDateTime.now());
    }
}
