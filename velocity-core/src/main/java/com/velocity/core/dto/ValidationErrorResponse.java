package com.velocity.core.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * Specialized error response for validation errors.
 * Extends ErrorResponse to include detailed field-level validation errors.
 * 
 * @author VeloCity Team
 * @version 1.0
 * @since 2026-02-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class ValidationErrorResponse extends ErrorResponse {
    
    /**
     * List of detailed validation errors
     */
    private List<FieldError> errors;
    
    /**
     * Represents a single field validation error
     */
    @Data
    @Accessors(chain = true)
    public static class FieldError {
        /**
         * Name of the field that failed validation
         */
        private String field;
        
        /**
         * The rejected value
         */
        private Object rejectedValue;
        
        /**
         * Error message describing why validation failed
         */
        private String message;
        
        /**
         * Validation constraint that was violated (e.g., NotNull, Size)
         */
        private String constraint;
    }
    
    /**
     * Creates a validation error response
     * 
     * @param message Overall error message
     * @param path Request path
     * @return ValidationErrorResponse instance
     */
    public static ValidationErrorResponse of(String message, String path) {
        ValidationErrorResponse response = new ValidationErrorResponse();
        response.setErrorCode("VEL-1001");
        response.setMessage(message);
        response.setPath(path);
        response.setTimestamp(LocalDateTime.now());
        response.setErrors(new ArrayList<>());
        return response;
    }
    
    /**
     * Adds a field error to the response
     * 
     * @param field Field name
     * @param rejectedValue The rejected value
     * @param message Error message
     * @param constraint Constraint type
     * @return This instance for chaining
     */
    public ValidationErrorResponse addFieldError(
            String field, 
            Object rejectedValue, 
            String message,
            String constraint) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(new FieldError()
                .setField(field)
                .setRejectedValue(rejectedValue)
                .setMessage(message)
                .setConstraint(constraint));
        return this;
    }
    
    /**
     * Adds a simple field error without rejected value
     * 
     * @param field Field name
     * @param message Error message
     * @return This instance for chaining
     */
    public ValidationErrorResponse addFieldError(String field, String message) {
        return addFieldError(field, null, message, null);
    }
    
    /**
     * Converts field errors to a simple map for backward compatibility
     * 
     * @return Map of field name to error message
     */
    public Map<String, String> getFieldErrorsMap() {
        if (errors == null || errors.isEmpty()) {
            return new HashMap<>();
        }
        Map<String, String> map = new HashMap<>();
        errors.forEach(error -> map.put(error.getField(), error.getMessage()));
        return map;
    }
}
