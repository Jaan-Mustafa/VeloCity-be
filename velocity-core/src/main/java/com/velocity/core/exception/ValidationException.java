package com.velocity.core.exception;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * Exception thrown when validation fails.
 * Used for input validation errors at the edge (controller level).
 * 
 * @author VeloCity Team
 * @since 1.0.0
 */
@Getter
public class ValidationException extends RuntimeException {
    
    private final Map<String, String> fieldErrors;
    
    /**
     * Constructor with simple message.
     *
     * @param message the validation error message
     */
    public ValidationException(String message) {
        super(message);
        this.fieldErrors = new HashMap<>();
    }
    
    /**
     * Constructor with field-specific error.
     *
     * @param field the field that failed validation
     * @param error the validation error message
     */
    public ValidationException(String field, String error) {
        super(String.format("Validation failed for field '%s': %s", field, error));
        this.fieldErrors = new HashMap<>();
        this.fieldErrors.put(field, error);
    }
    
    /**
     * Constructor with multiple field errors.
     *
     * @param message the general validation error message
     * @param fieldErrors map of field names to error messages
     */
    public ValidationException(String message, Map<String, String> fieldErrors) {
        super(message);
        this.fieldErrors = fieldErrors != null ? new HashMap<>(fieldErrors) : new HashMap<>();
    }
}
