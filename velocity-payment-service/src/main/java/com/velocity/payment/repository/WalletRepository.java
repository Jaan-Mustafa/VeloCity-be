package com.velocity.payment.repository;

import com.velocity.payment.model.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Wallet entity.
 * Provides CRUD operations and custom queries for wallet management.
 * 
 * Following rules.md:
 * - Extends JpaRepository for standard operations
 * - Uses pessimistic locking for balance updates
 * - Optimistic locking via @Version field in entity
 */
@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {
    
    /**
     * Find wallet by user ID
     * @param userId User ID
     * @return Optional containing wallet if found
     */
    Optional<Wallet> findByUserId(Long userId);
    
    /**
     * Find wallet by user ID with pessimistic write lock
     * Used for balance updates to prevent concurrent modification
     * @param userId User ID
     * @return Optional containing wallet if found
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.userId = :userId")
    Optional<Wallet> findByUserIdForUpdate(@Param("userId") Long userId);
    
    /**
     * Check if wallet exists for user
     * @param userId User ID
     * @return true if wallet exists
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Find all active wallets
     * @return List of active wallets
     */
    List<Wallet> findByIsActiveTrue();
    
    /**
     * Find wallets with balance greater than specified amount
     * @param minBalance Minimum balance
     * @return List of wallets
     */
    @Query("SELECT w FROM Wallet w WHERE w.balance >= :minBalance AND w.isActive = true")
    List<Wallet> findByBalanceGreaterThanEqual(@Param("minBalance") BigDecimal minBalance);
    
    /**
     * Find wallets with low balance (below threshold)
     * @param threshold Balance threshold
     * @return List of wallets with low balance
     */
    @Query("SELECT w FROM Wallet w WHERE w.balance < :threshold AND w.isActive = true")
    List<Wallet> findLowBalanceWallets(@Param("threshold") BigDecimal threshold);
    
    /**
     * Calculate total balance across all active wallets
     * @return Total balance
     */
    @Query("SELECT SUM(w.balance) FROM Wallet w WHERE w.isActive = true")
    BigDecimal calculateTotalBalance();
}
