package com.velocity.user.repository;

import com.velocity.core.enums.UserRole;
import com.velocity.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 * Provides CRUD operations and custom queries for user management.
 * 
 * Following rules.md:
 * - Extends JpaRepository for standard operations
 * - Custom query methods using Spring Data naming conventions
 * - Returns Optional for single results
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email address
     * @param email Email address
     * @return Optional containing user if found
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by phone number
     * @param phone Phone number
     * @return Optional containing user if found
     */
    Optional<User> findByPhone(String phone);
    
    /**
     * Find user by email or phone
     * @param email Email address
     * @param phone Phone number
     * @return Optional containing user if found
     */
    Optional<User> findByEmailOrPhone(String email, String phone);
    
    /**
     * Check if email already exists
     * @param email Email address
     * @return true if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if phone already exists
     * @param phone Phone number
     * @return true if phone exists
     */
    boolean existsByPhone(String phone);
    
    /**
     * Find all users by role
     * @param role User role
     * @return List of users with the specified role
     */
    java.util.List<User> findByRole(UserRole role);
    
    /**
     * Find all active users
     * @return List of active users
     */
    java.util.List<User> findByIsActiveTrue();
    
    /**
     * Find all verified users
     * @return List of verified users
     */
    java.util.List<User> findByIsVerifiedTrue();
}
