package com.velocity.core.util;

import java.util.regex.Pattern;

import com.velocity.core.exception.ValidationException;

/**
 * Utility class for input validation.
 * Provides helper methods for validating common input types like phone numbers, emails, etc.
 * 
 * <p>All methods are static and the class cannot be instantiated.</p>
 * 
 * @author VeloCity Team
 * @version 1.0
 * @since 2026-02-01
 */
public final class ValidationUtil {

    /**
     * Pattern for validating Indian mobile numbers.
     * Accepts formats: 9876543210, +919876543210, 919876543210
     */
    private static final Pattern INDIAN_MOBILE_PATTERN = Pattern.compile(
        "^(\\+91|91)?[6-9]\\d{9}$"
    );

    /**
     * Pattern for validating email addresses.
     * Basic email validation pattern.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Pattern for validating vehicle registration numbers (Indian format).
     * Format: XX00XX0000 (e.g., MH12AB1234)
     */
    private static final Pattern VEHICLE_REGISTRATION_PATTERN = Pattern.compile(
        "^[A-Z]{2}[0-9]{2}[A-Z]{1,2}[0-9]{4}$"
    );

    /**
     * Pattern for validating PAN card numbers (Indian format).
     * Format: XXXXX0000X (e.g., ABCDE1234F)
     */
    private static final Pattern PAN_PATTERN = Pattern.compile(
        "^[A-Z]{5}[0-9]{4}[A-Z]$"
    );

    /**
     * Pattern for validating Aadhaar numbers (Indian format).
     * Format: 12 digits
     */
    private static final Pattern AADHAAR_PATTERN = Pattern.compile(
        "^[0-9]{12}$"
    );

    /**
     * Pattern for validating IFSC codes (Indian banking).
     * Format: XXXX0XXXXXX (e.g., SBIN0001234)
     */
    private static final Pattern IFSC_PATTERN = Pattern.compile(
        "^[A-Z]{4}0[A-Z0-9]{6}$"
    );

    /**
     * Pattern for validating alphanumeric strings (letters and numbers only).
     */
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile(
        "^[A-Za-z0-9]+$"
    );

    /**
     * Minimum password length.
     */
    private static final int MIN_PASSWORD_LENGTH = 8;

    /**
     * Maximum password length.
     */
    private static final int MAX_PASSWORD_LENGTH = 128;

    /**
     * Private constructor to prevent instantiation.
     * 
     * @throws UnsupportedOperationException if instantiation is attempted
     */
    private ValidationUtil() {
        throw new UnsupportedOperationException("ValidationUtil is a utility class and cannot be instantiated");
    }

    /**
     * Validates an Indian mobile number.
     * 
     * @param phoneNumber the phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidIndianMobile(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return INDIAN_MOBILE_PATTERN.matcher(phoneNumber.trim()).matches();
    }

    /**
     * Validates an Indian mobile number and throws exception if invalid.
     * 
     * @param phoneNumber the phone number to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the phone number is invalid
     */
    public static void validateIndianMobile(String phoneNumber, String fieldName) {
        if (!isValidIndianMobile(phoneNumber)) {
            throw new ValidationException(
                fieldName,
                "Invalid Indian mobile number. Must be 10 digits starting with 6-9"
            );
        }
    }

    /**
     * Normalizes an Indian mobile number to 10-digit format.
     * Removes +91 or 91 prefix if present.
     * 
     * @param phoneNumber the phone number to normalize
     * @return normalized 10-digit phone number
     * @throws ValidationException if the phone number is invalid
     */
    public static String normalizeIndianMobile(String phoneNumber) {
        if (!isValidIndianMobile(phoneNumber)) {
            throw new ValidationException("Invalid Indian mobile number");
        }
        
        String normalized = phoneNumber.trim().replaceAll("\\s+", "");
        
        // Remove +91 or 91 prefix
        if (normalized.startsWith("+91")) {
            normalized = normalized.substring(3);
        } else if (normalized.startsWith("91") && normalized.length() == 12) {
            normalized = normalized.substring(2);
        }
        
        return normalized;
    }

    /**
     * Validates an email address.
     * 
     * @param email the email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates an email address and throws exception if invalid.
     * 
     * @param email the email to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the email is invalid
     */
    public static void validateEmail(String email, String fieldName) {
        if (!isValidEmail(email)) {
            throw new ValidationException(fieldName, "Invalid email address format");
        }
    }

    /**
     * Validates a vehicle registration number (Indian format).
     * 
     * @param registrationNumber the registration number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidVehicleRegistration(String registrationNumber) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            return false;
        }
        String normalized = registrationNumber.trim().replaceAll("\\s+", "").toUpperCase();
        return VEHICLE_REGISTRATION_PATTERN.matcher(normalized).matches();
    }

    /**
     * Validates a vehicle registration number and throws exception if invalid.
     * 
     * @param registrationNumber the registration number to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the registration number is invalid
     */
    public static void validateVehicleRegistration(String registrationNumber, String fieldName) {
        if (!isValidVehicleRegistration(registrationNumber)) {
            throw new ValidationException(
                fieldName,
                "Invalid vehicle registration number. Format: XX00XX0000 (e.g., MH12AB1234)"
            );
        }
    }

    /**
     * Normalizes a vehicle registration number to uppercase without spaces.
     * 
     * @param registrationNumber the registration number to normalize
     * @return normalized registration number
     * @throws ValidationException if the registration number is invalid
     */
    public static String normalizeVehicleRegistration(String registrationNumber) {
        if (!isValidVehicleRegistration(registrationNumber)) {
            throw new ValidationException("Invalid vehicle registration number");
        }
        return registrationNumber.trim().replaceAll("\\s+", "").toUpperCase();
    }

    /**
     * Validates a PAN card number (Indian format).
     * 
     * @param panNumber the PAN number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPan(String panNumber) {
        if (panNumber == null || panNumber.trim().isEmpty()) {
            return false;
        }
        return PAN_PATTERN.matcher(panNumber.trim().toUpperCase()).matches();
    }

    /**
     * Validates a PAN card number and throws exception if invalid.
     * 
     * @param panNumber the PAN number to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the PAN number is invalid
     */
    public static void validatePan(String panNumber, String fieldName) {
        if (!isValidPan(panNumber)) {
            throw new ValidationException(
                fieldName,
                "Invalid PAN number. Format: XXXXX0000X (e.g., ABCDE1234F)"
            );
        }
    }

    /**
     * Validates an Aadhaar number (Indian format).
     * 
     * @param aadhaarNumber the Aadhaar number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAadhaar(String aadhaarNumber) {
        if (aadhaarNumber == null || aadhaarNumber.trim().isEmpty()) {
            return false;
        }
        String normalized = aadhaarNumber.trim().replaceAll("\\s+", "");
        return AADHAAR_PATTERN.matcher(normalized).matches();
    }

    /**
     * Validates an Aadhaar number and throws exception if invalid.
     * 
     * @param aadhaarNumber the Aadhaar number to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the Aadhaar number is invalid
     */
    public static void validateAadhaar(String aadhaarNumber, String fieldName) {
        if (!isValidAadhaar(aadhaarNumber)) {
            throw new ValidationException(fieldName, "Invalid Aadhaar number. Must be 12 digits");
        }
    }

    /**
     * Validates an IFSC code (Indian banking).
     * 
     * @param ifscCode the IFSC code to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidIfsc(String ifscCode) {
        if (ifscCode == null || ifscCode.trim().isEmpty()) {
            return false;
        }
        return IFSC_PATTERN.matcher(ifscCode.trim().toUpperCase()).matches();
    }

    /**
     * Validates an IFSC code and throws exception if invalid.
     * 
     * @param ifscCode the IFSC code to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the IFSC code is invalid
     */
    public static void validateIfsc(String ifscCode, String fieldName) {
        if (!isValidIfsc(ifscCode)) {
            throw new ValidationException(
                fieldName,
                "Invalid IFSC code. Format: XXXX0XXXXXX (e.g., SBIN0001234)"
            );
        }
    }

    /**
     * Validates a password strength.
     * Password must be 8-128 characters and contain at least:
     * - One uppercase letter
     * - One lowercase letter
     * - One digit
     * - One special character
     * 
     * @param password the password to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH || 
            password.length() > MAX_PASSWORD_LENGTH) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    /**
     * Validates a password and throws exception if invalid.
     * 
     * @param password the password to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the password is invalid
     */
    public static void validatePassword(String password, String fieldName) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException(
                fieldName,
                "Password must be at least " + MIN_PASSWORD_LENGTH + " characters long"
            );
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new ValidationException(
                fieldName,
                "Password must not exceed " + MAX_PASSWORD_LENGTH + " characters"
            );
        }
        if (!isValidPassword(password)) {
            throw new ValidationException(
                fieldName,
                "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character"
            );
        }
    }

    /**
     * Validates that a string is not null or empty.
     * 
     * @param value the value to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the value is null or empty
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName, fieldName + " cannot be empty");
        }
    }

    /**
     * Validates that a string length is within the specified range.
     * 
     * @param value the value to validate
     * @param fieldName the name of the field (for error message)
     * @param minLength the minimum length (inclusive)
     * @param maxLength the maximum length (inclusive)
     * @throws ValidationException if the length is out of range
     */
    public static void validateLength(String value, String fieldName, int minLength, int maxLength) {
        validateNotEmpty(value, fieldName);
        int length = value.trim().length();
        if (length < minLength || length > maxLength) {
            throw new ValidationException(
                fieldName,
                fieldName + " must be between " + minLength + " and " + maxLength + " characters"
            );
        }
    }

    /**
     * Validates that a number is within the specified range.
     * 
     * @param value the value to validate
     * @param fieldName the name of the field (for error message)
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     * @throws ValidationException if the value is out of range
     */
    public static void validateRange(int value, String fieldName, int min, int max) {
        if (value < min || value > max) {
            throw new ValidationException(
                fieldName,
                fieldName + " must be between " + min + " and " + max
            );
        }
    }

    /**
     * Validates that a number is within the specified range.
     * 
     * @param value the value to validate
     * @param fieldName the name of the field (for error message)
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     * @throws ValidationException if the value is out of range
     */
    public static void validateRange(double value, String fieldName, double min, double max) {
        if (value < min || value > max) {
            throw new ValidationException(
                fieldName,
                fieldName + " must be between " + min + " and " + max
            );
        }
    }

    /**
     * Validates that a number is positive (greater than zero).
     * 
     * @param value the value to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the value is not positive
     */
    public static void validatePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName, fieldName + " must be positive");
        }
    }

    /**
     * Validates that a number is positive (greater than zero).
     * 
     * @param value the value to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the value is not positive
     */
    public static void validatePositive(double value, String fieldName) {
        if (value <= 0) {
            throw new ValidationException(fieldName, fieldName + " must be positive");
        }
    }

    /**
     * Validates that a number is non-negative (greater than or equal to zero).
     * 
     * @param value the value to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the value is negative
     */
    public static void validateNonNegative(int value, String fieldName) {
        if (value < 0) {
            throw new ValidationException(fieldName, fieldName + " cannot be negative");
        }
    }

    /**
     * Validates that a number is non-negative (greater than or equal to zero).
     * 
     * @param value the value to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the value is negative
     */
    public static void validateNonNegative(double value, String fieldName) {
        if (value < 0) {
            throw new ValidationException(fieldName, fieldName + " cannot be negative");
        }
    }

    /**
     * Validates that a string contains only alphanumeric characters.
     * 
     * @param value the value to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the value contains non-alphanumeric characters
     */
    public static void validateAlphanumeric(String value, String fieldName) {
        validateNotEmpty(value, fieldName);
        if (!ALPHANUMERIC_PATTERN.matcher(value.trim()).matches()) {
            throw new ValidationException(
                fieldName,
                fieldName + " must contain only letters and numbers"
            );
        }
    }

    /**
     * Validates latitude coordinate.
     * 
     * @param latitude the latitude to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the latitude is invalid
     */
    public static void validateLatitude(double latitude, String fieldName) {
        if (latitude < -90.0 || latitude > 90.0) {
            throw new ValidationException(
                fieldName,
                "Latitude must be between -90 and 90 degrees"
            );
        }
    }

    /**
     * Validates longitude coordinate.
     * 
     * @param longitude the longitude to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the longitude is invalid
     */
    public static void validateLongitude(double longitude, String fieldName) {
        if (longitude < -180.0 || longitude > 180.0) {
            throw new ValidationException(
                fieldName,
                "Longitude must be between -180 and 180 degrees"
            );
        }
    }

    /**
     * Validates coordinates (latitude and longitude).
     * 
     * @param latitude the latitude to validate
     * @param longitude the longitude to validate
     * @throws ValidationException if any coordinate is invalid
     */
    public static void validateCoordinates(double latitude, double longitude) {
        validateLatitude(latitude, "latitude");
        validateLongitude(longitude, "longitude");
    }

    /**
     * Validates that an object is not null.
     * 
     * @param value the value to validate
     * @param fieldName the name of the field (for error message)
     * @throws ValidationException if the value is null
     */
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName, fieldName + " cannot be null");
        }
    }

    /**
     * Sanitizes a string by trimming whitespace and removing potentially harmful characters.
     * Useful for preventing XSS attacks.
     * 
     * @param input the input string to sanitize
     * @return sanitized string, or null if input is null
     */
    public static String sanitize(String input) {
        if (input == null) {
            return null;
        }
        
        // Trim whitespace
        String sanitized = input.trim();
        
        // Remove potentially harmful characters
        sanitized = sanitized.replaceAll("[<>\"'&]", "");
        
        return sanitized;
    }

    /**
     * Checks if a string is null or empty (after trimming).
     * 
     * @param value the value to check
     * @return true if null or empty, false otherwise
     */
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    /**
     * Checks if a string is not null and not empty (after trimming).
     * 
     * @param value the value to check
     * @return true if not null and not empty, false otherwise
     */
    public static boolean isNotEmpty(String value) {
        return !isNullOrEmpty(value);
    }
}
