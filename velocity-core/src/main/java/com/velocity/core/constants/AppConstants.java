package com.velocity.core.constants;

public class AppConstants {
    public static final String API_VERSION = "v1";
    public static final String API_BASE_PATH = "/api/" + API_VERSION;
    
    // Pagination
    public static final String DEFAULT_PAGE_NUMBER = "0";
    public static final String DEFAULT_PAGE_SIZE = "10";
    public static final String DEFAULT_SORT_BY = "id";
    public static final String DEFAULT_SORT_DIRECTION = "asc";
    
    private AppConstants() {
        // Private constructor to prevent instantiation
    }
}
