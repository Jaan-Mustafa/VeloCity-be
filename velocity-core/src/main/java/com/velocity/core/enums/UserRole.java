package com.velocity.core.enums;

import lombok.Getter;

import java.util.Set;

/**
 * User roles in the VeloCity system.
 * Each role has specific permissions and capabilities.
 */
@Getter
public enum UserRole {
    
    RIDER("Rider", Set.of(
        "BOOK_RIDE",
        "VIEW_RIDE_HISTORY",
        "MANAGE_WALLET",
        "UPDATE_PROFILE"
    )),
    
    DRIVER("Driver", Set.of(
        "ACCEPT_RIDE",
        "START_RIDE",
        "COMPLETE_RIDE",
        "UPDATE_LOCATION",
        "TOGGLE_AVAILABILITY",
        "VIEW_EARNINGS",
        "UPDATE_PROFILE"
    )),
    
    ADMIN("Admin", Set.of(
        "MANAGE_USERS",
        "MANAGE_DRIVERS",
        "VIEW_ALL_RIDES",
        "MANAGE_PAYMENTS",
        "VIEW_ANALYTICS",
        "SYSTEM_CONFIGURATION"
    ));
    
    private final String displayName;
    private final Set<String> permissions;
    
    UserRole(String displayName, Set<String> permissions) {
        this.displayName = displayName;
        this.permissions = permissions;
    }
    
    /**
     * Check if this role has a specific permission.
     * @param permission the permission to check
     * @return true if the role has the permission
     */
    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
    
    /**
     * Check if this is a driver role.
     * @return true if the role is DRIVER
     */
    public boolean isDriver() {
        return this == DRIVER;
    }
    
    /**
     * Check if this is a rider role.
     * @return true if the role is RIDER
     */
    public boolean isRider() {
        return this == RIDER;
    }
    
    /**
     * Check if this is an admin role.
     * @return true if the role is ADMIN
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }
}
