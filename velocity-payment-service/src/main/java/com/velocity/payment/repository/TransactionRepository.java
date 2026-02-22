package com.velocity.payment.repository;

import com.velocity.core.enums.TransactionType;
import com.velocity.payment.model.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Transaction entity.
 * Provides CRUD operations and custom queries for transaction management.
 * 
 * Following rules.md:
 * - Extends JpaRepository for standard operations
 * - Paginated queries for transaction history
 * - Aggregate queries for analytics
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    
    /**
     * Find transactions by wallet ID with pagination
     * @param walletId Wallet ID
     * @param pageable Pagination parameters
     * @return Page of transactions
     */
    Page<Transaction> findByWalletId(Long walletId, Pageable pageable);
    
    /**
     * Find transactions by ride ID
     * @param rideId Ride ID
     * @return List of transactions
     */
    List<Transaction> findByRideId(Long rideId);
    
    /**
     * Find transactions by type
     * @param type Transaction type
     * @param pageable Pagination parameters
     * @return Page of transactions
     */
    Page<Transaction> findByType(TransactionType type, Pageable pageable);
    
    /**
     * Find transactions by wallet and type
     * @param walletId Wallet ID
     * @param type Transaction type
     * @param pageable Pagination parameters
     * @return Page of transactions
     */
    Page<Transaction> findByWalletIdAndType(Long walletId, TransactionType type, Pageable pageable);
    
    /**
     * Find transactions by reference ID
     * @param referenceId Reference ID
     * @return Optional containing transaction if found
     */
    Optional<Transaction> findByReferenceId(String referenceId);
    
    /**
     * Find transactions within date range
     * @param walletId Wallet ID
     * @param startDate Start date
     * @param endDate End date
     * @param pageable Pagination parameters
     * @return Page of transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.walletId = :walletId AND t.createdAt BETWEEN :startDate AND :endDate ORDER BY t.createdAt DESC")
    Page<Transaction> findByWalletIdAndDateRange(
        @Param("walletId") Long walletId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate,
        Pageable pageable
    );
    
    /**
     * Calculate total amount by wallet and type
     * @param walletId Wallet ID
     * @param type Transaction type
     * @return Total amount
     */
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.walletId = :walletId AND t.type = :type AND t.status = 'COMPLETED'")
    BigDecimal calculateTotalByWalletAndType(@Param("walletId") Long walletId, @Param("type") TransactionType type);
    
    /**
     * Count transactions by wallet
     * @param walletId Wallet ID
     * @return Count of transactions
     */
    long countByWalletId(Long walletId);
    
    /**
     * Find recent transactions
     * @param walletId Wallet ID
     * @param pageable Pagination parameters (use PageRequest.of(0, limit) for limiting results)
     * @return List of recent transactions
     */
    @Query("SELECT t FROM Transaction t WHERE t.walletId = :walletId ORDER BY t.createdAt DESC")
    List<Transaction> findRecentTransactions(@Param("walletId") Long walletId, Pageable pageable);
}
