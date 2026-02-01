package com.velocity.core.enums;

import lombok.Getter;

import java.util.Set;

/**
 * Ride status lifecycle in the VeloCity system.
 * Defines valid state transitions and terminal states.
 */
@Getter
public enum RideStatus {
    
    REQUESTED("Requested", false, Set.of("ACCEPTED", "CANCELLED")),
    ACCEPTED("Accepted", false, Set.of("ARRIVED", "CANCELLED")),
    ARRIVED("Driver Arrived", false, Set.of("IN_PROGRESS", "CANCELLED")),
    IN_PROGRESS("In Progress", false, Set.of("COMPLETED", "CANCELLED")),
    COMPLETED("Completed", true, Set.of()),
    CANCELLED("Cancelled", true, Set.of());
    
    private final String displayName;
    private final boolean terminal;
    private final Set<String> allowedTransitions;
    
    RideStatus(String displayName, boolean terminal, Set<String> allowedTransitions) {
        this.displayName = displayName;
        this.terminal = terminal;
        this.allowedTransitions = allowedTransitions;
    }
    
    /**
     * Check if transition to a new status is allowed.
     * @param newStatus the target status
     * @return true if transition is allowed
     */
    public boolean canTransitionTo(RideStatus newStatus) {
        if (this.terminal) {
            return false; // Terminal states cannot transition
        }
        return allowedTransitions.contains(newStatus.name());
    }
    
    /**
     * Check if this is a terminal state.
     * @return true if the status is terminal (COMPLETED or CANCELLED)
     */
    public boolean isTerminal() {
        return terminal;
    }
    
    /**
     * Check if the ride is active (not terminal).
     * @return true if the ride is in progress
     */
    public boolean isActive() {
        return !terminal;
    }
    
    /**
     * Check if the ride can be cancelled.
     * @return true if cancellation is allowed
     */
    public boolean canBeCancelled() {
        return !terminal && allowedTransitions.contains("CANCELLED");
    }
    
    /**
     * Check if the ride is in a state where driver is assigned.
     * @return true if status is ACCEPTED or later
     */
    public boolean hasDriverAssigned() {
        return this != REQUESTED && this != CANCELLED;
    }
}
