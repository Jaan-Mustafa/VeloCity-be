package com.velocity.core.constants;

/**
 * Application-wide constants for the VeloCity system.
 * Following rules.md: Utility classes must have private constructors.
 */
public class AppConstants {
    
    // API Configuration
    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api/" + API_VERSION;
    
    // Pagination Defaults
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";
    public static final int MAX_PAGE_SIZE = 100;
    
    // Ride Constants
    public static final int DRIVER_SEARCH_RADIUS_KM = 5;
    public static final int DRIVER_ACCEPT_TIMEOUT_SECONDS = 30;
    public static final int RIDE_AUTO_CANCEL_MINUTES = 5;
    public static final int MAX_DRIVERS_TO_NOTIFY = 3;
    
    // Location Constants
    public static final int LOCATION_UPDATE_INTERVAL_SECONDS = 10;
    public static final int LOCATION_TTL_SECONDS = 30;
    public static final double MIN_LATITUDE = -90.0;
    public static final double MAX_LATITUDE = 90.0;
    public static final double MIN_LONGITUDE = -180.0;
    public static final double MAX_LONGITUDE = 180.0;
    
    // Payment Constants
    public static final String DEFAULT_CURRENCY = "INR";
    public static final double MIN_WALLET_BALANCE = 0.0;
    public static final double MAX_WALLET_BALANCE = 100000.0;
    public static final double MIN_TRANSACTION_AMOUNT = 1.0;
    
    // Validation Patterns
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final String PHONE_PATTERN = "^[6-9]\\d{9}$"; // Indian mobile number
    public static final String LICENSE_PATTERN = "^[A-Z]{2}\\d{13}$"; // Indian driving license
    
    // Security Constants
    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 100;
    public static final int MAX_LOGIN_ATTEMPTS = 5;
    public static final int ACCOUNT_LOCK_DURATION_MINUTES = 30;
    
    // JWT Constants
    public static final long ACCESS_TOKEN_VALIDITY_MINUTES = 15;
    public static final long REFRESH_TOKEN_VALIDITY_DAYS = 7;
    
    // Cache Constants
    public static final int CACHE_TTL_MINUTES = 10;
    public static final String CACHE_KEY_PREFIX = "velocity:";
    
    // Notification Constants
    public static final int MAX_NOTIFICATION_RETRY_ATTEMPTS = 3;
    public static final int NOTIFICATION_RETRY_DELAY_SECONDS = 5;
    
    private AppConstants() {
        // Private constructor to prevent instantiation (following rules.md)
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
