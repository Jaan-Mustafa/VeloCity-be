package com.velocity.core.exception;

import com.velocity.core.constants.ErrorCodes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all REST controllers.
 * Catches exceptions and returns standardized error responses.
 * 
 * Note to Dev: This handler ensures consistent error responses across all microservices.
 * All exceptions are logged with full stack traces for debugging.
 * 
 * @author VeloCity Team
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle validation exceptions from @Valid annotations.
     *
     * @param ex the validation exception
     * @param request the web request
     * @return standardized error response with field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        log.error("Validation error occurred: {}", ex.getMessage(), ex);
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodes.VALIDATION_ERROR.getFullCode())
                .message("Validation failed for one or more fields")
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .fieldErrors(fieldErrors)
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle custom validation exceptions.
     *
     * @param ex the validation exception
     * @param request the web request
     * @return standardized error response
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            WebRequest request) {
        
        log.error("Validation exception: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodes.VALIDATION_ERROR.getFullCode())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .fieldErrors(ex.getFieldErrors())
                .build();
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle unauthorized access exceptions.
     *
     * @param ex the unauthorized exception
     * @param request the web request
     * @return standardized error response
     */
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(
            UnauthorizedException ex,
            WebRequest request) {
        
        log.error("Unauthorized access: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodes.UNAUTHORIZED.getFullCode())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
    }
    
    /**
     * Handle resource not found exceptions.
     *
     * @param ex the resource not found exception
     * @param request the web request
     * @return standardized error response
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            WebRequest request) {
        
        log.error("Resource not found: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodes.RESOURCE_NOT_FOUND.getFullCode())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handle business logic exceptions.
     *
     * @param ex the business exception
     * @param request the web request
     * @return standardized error response
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            WebRequest request) {
        
        log.error("Business exception: {} - {}", ex.getFullErrorCode(), ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getFullErrorCode())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);
    }
    
    /**
     * Handle insufficient balance exceptions.
     *
     * @param ex the insufficient balance exception
     * @param request the web request
     * @return standardized error response with balance details
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(
            InsufficientBalanceException ex,
            WebRequest request) {
        
        log.error("Insufficient balance: Required={}, Available={}, Shortfall={}", 
                ex.getRequiredAmount(), ex.getAvailableBalance(), ex.getShortfall(), ex);
        
        Map<String, String> details = new HashMap<>();
        details.put("requiredAmount", ex.getRequiredAmount().toString());
        details.put("availableBalance", ex.getAvailableBalance().toString());
        details.put("shortfall", ex.getShortfall().toString());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getFullErrorCode())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .fieldErrors(details)
                .build();
        
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(errorResponse);
    }
    
    /**
     * Handle driver not available exceptions.
     *
     * @param ex the driver not available exception
     * @param request the web request
     * @return standardized error response
     */
    @ExceptionHandler(DriverNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleDriverNotAvailableException(
            DriverNotAvailableException ex,
            WebRequest request) {
        
        log.error("Driver not available: {}", ex.getMessage(), ex);
        
        Map<String, String> details = new HashMap<>();
        if (ex.getLatitude() != null) {
            details.put("latitude", ex.getLatitude().toString());
            details.put("longitude", ex.getLongitude().toString());
            details.put("radiusKm", ex.getRadiusKm().toString());
        }
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ex.getFullErrorCode())
                .message(ex.getMessage())
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .fieldErrors(details.isEmpty() ? null : details)
                .build();
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
    
    /**
     * Handle all other uncaught exceptions.
     * Note to Dev: This is the catch-all handler. Always log full stack trace.
     *
     * @param ex the exception
     * @param request the web request
     * @return standardized error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .errorCode(ErrorCodes.INTERNAL_SERVER_ERROR.getFullCode())
                .message("An unexpected error occurred. Please try again later.")
                .path(request.getDescription(false).replace("uri=", ""))
                .timestamp(LocalDateTime.now())
                .build();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
    
    /**
     * Standardized error response structure.
     */
    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class ErrorResponse {
        private String errorCode;
        private String message;
        private String path;
        private LocalDateTime timestamp;
        private Map<String, String> fieldErrors;
    }
}
