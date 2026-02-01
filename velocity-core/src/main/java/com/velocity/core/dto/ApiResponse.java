package com.velocity.core.dto;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Generic wrapper for all API responses.
 * Provides a consistent structure for successful API responses across all services.
 * 
 * @param <T> The type of data being returned
 * 
 * @author VeloCity Team
 * @version 1.0
 * @since 2026-02-01
 */
@Data
@Accessors(chain = true)
public class ApiResponse<T> {
    
    /**
     * Indicates whether the operation was successful
     */
    private boolean success;
    
    /**
     * Human-readable message describing the result
     */
    private String message;
    
    /**
     * The actual data payload
     */
    private T data;
    
    /**
     * Timestamp when the response was generated
     */
    private LocalDateTime timestamp;
    
    /**
     * Creates a successful response with data
     * 
     * @param data The data to return
     * @param message Success message
     * @param <T> Type of data
     * @return ApiResponse with success=true
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<T>()
                .setSuccess(true)
                .setMessage(message)
                .setData(data)
                .setTimestamp(LocalDateTime.now());
    }
    
    /**
     * Creates a successful response with data and default message
     * 
     * @param data The data to return
     * @param <T> Type of data
     * @return ApiResponse with success=true
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation completed successfully");
    }
    
    /**
     * Creates a successful response without data
     * 
     * @param message Success message
     * @param <T> Type of data
     * @return ApiResponse with success=true and null data
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<T>()
                .setSuccess(true)
                .setMessage(message)
                .setTimestamp(LocalDateTime.now());
    }
    
    /**
     * Creates a failure response
     * 
     * @param message Error message
     * @param <T> Type of data
     * @return ApiResponse with success=false
     */
    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<T>()
                .setSuccess(false)
                .setMessage(message)
                .setTimestamp(LocalDateTime.now());
    }
}
