package com.velocity.payment.service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.velocity.core.enums.TransactionType;
import com.velocity.core.exception.InsufficientBalanceException;
import com.velocity.core.exception.ResourceNotFoundException;
import com.velocity.payment.mapper.WalletMapper;
import com.velocity.payment.model.dto.AddMoneyRequest;
import com.velocity.payment.model.dto.DeductMoneyRequest;
import com.velocity.payment.model.dto.PaymentResponse;
import com.velocity.payment.model.dto.RefundRequest;
import com.velocity.payment.model.dto.TransactionDto;
import com.velocity.payment.model.dto.WalletDto;
import com.velocity.payment.model.entity.Transaction;
import com.velocity.payment.model.entity.Wallet;
import com.velocity.payment.repository.TransactionRepository;
import com.velocity.payment.repository.WalletRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service for wallet and transaction operations.
 * Handles wallet creation, balance management, and transaction history.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WalletService {
    
    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;
    private final WalletMapper walletMapper;
    
    /**
     * Create a new wallet for a user
     */
    @Transactional
    public WalletDto createWallet(Long userId) {
        log.info("Creating wallet for user: {}", userId);
        
        // Check if wallet already exists
        if (walletRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("Wallet already exists for user: " + userId);
        }
        
        Wallet wallet = new Wallet();
        wallet.setUserId(userId);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setIsActive(true);
        
        Wallet savedWallet = walletRepository.save(wallet);
        log.info("Wallet created successfully for user: {} with ID: {}", userId, savedWallet.getId());
        
        return walletMapper.toDto(savedWallet);
    }
    
    /**
     * Get wallet by user ID
     */
    @Transactional(readOnly = true)
    public WalletDto getWalletByUserId(Long userId) {
        log.debug("Fetching wallet for user: {}", userId);
        
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        
        return walletMapper.toDto(wallet);
    }
    
    /**
     * Get wallet balance
     */
    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long userId) {
        log.debug("Fetching balance for user: {}", userId);
        
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        
        return wallet.getBalance();
    }
    
    /**
     * Add money to wallet
     */
    @Transactional
    public PaymentResponse addMoney(Long userId, AddMoneyRequest request) {
        log.info("Adding money to wallet for user: {}, amount: {}", userId, request.getAmount());
        
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        
        if (!wallet.getIsActive()) {
            throw new IllegalStateException("Wallet is not active");
        }
        
        BigDecimal balanceBefore = wallet.getBalance();
        wallet.credit(request.getAmount());
        walletRepository.save(wallet);
        
        // Create transaction record
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("paymentMethod", request.getPaymentMethod());
        
        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setType(TransactionType.CREDIT);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(wallet.getBalance());
        transaction.setDescription("Money added to wallet");
        transaction.setReferenceId(request.getReferenceId());
        transaction.setStatus("COMPLETED");
        transaction.setMetadata(metadata);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("Money added successfully. Transaction ID: {}, New balance: {}", 
                savedTransaction.getId(), wallet.getBalance());
        
        return PaymentResponse.builder()
                .success(true)
                .message("Money added successfully")
                .transactionId(savedTransaction.getId())
                .newBalance(wallet.getBalance())
                .build();
    }
    
    /**
     * Deduct money from wallet (for ride payment)
     */
    @Transactional
    public PaymentResponse deductMoney(DeductMoneyRequest request) {
        log.info("Deducting money from wallet for user: {}, amount: {}, ride: {}", 
                request.getUserId(), request.getAmount(), request.getRideId());
        
        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + request.getUserId()));
        
        if (!wallet.getIsActive()) {
            throw new IllegalStateException("Wallet is not active");
        }
        
        if (!wallet.hasSufficientBalance(request.getAmount())) {
            throw new InsufficientBalanceException(request.getAmount(), wallet.getBalance());
        }
        
        BigDecimal balanceBefore = wallet.getBalance();
        wallet.debit(request.getAmount());
        walletRepository.save(wallet);
        
        // Create transaction record
        Map<String, Object> metadata = new HashMap<>();
        if (request.getRideId() != null) {
            metadata.put("rideId", request.getRideId());
        }
        
        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setRideId(request.getRideId());
        transaction.setType(TransactionType.DEBIT);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(wallet.getBalance());
        transaction.setDescription(request.getDescription() != null ? request.getDescription() : "Ride payment");
        transaction.setStatus("COMPLETED");
        transaction.setMetadata(metadata);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("Money deducted successfully. Transaction ID: {}, New balance: {}", 
                savedTransaction.getId(), wallet.getBalance());
        
        return PaymentResponse.builder()
                .success(true)
                .message("Payment processed successfully")
                .transactionId(savedTransaction.getId())
                .newBalance(wallet.getBalance())
                .build();
    }
    
    /**
     * Refund money to wallet (for ride cancellation)
     */
    @Transactional
    public PaymentResponse refundMoney(RefundRequest request) {
        log.info("Refunding money to wallet for user: {}, amount: {}, ride: {}", 
                request.getUserId(), request.getAmount(), request.getRideId());
        
        Wallet wallet = walletRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + request.getUserId()));
        
        BigDecimal balanceBefore = wallet.getBalance();
        wallet.credit(request.getAmount());
        walletRepository.save(wallet);
        
        // Create transaction record
        Map<String, Object> metadata = new HashMap<>();
        if (request.getRideId() != null) {
            metadata.put("rideId", request.getRideId());
        }
        if (request.getReason() != null) {
            metadata.put("reason", request.getReason());
        }
        
        Transaction transaction = new Transaction();
        transaction.setWalletId(wallet.getId());
        transaction.setRideId(request.getRideId());
        transaction.setType(TransactionType.REFUND);
        transaction.setAmount(request.getAmount());
        transaction.setBalanceBefore(balanceBefore);
        transaction.setBalanceAfter(wallet.getBalance());
        transaction.setDescription("Refund for ride cancellation");
        transaction.setStatus("COMPLETED");
        transaction.setMetadata(metadata);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        log.info("Refund processed successfully. Transaction ID: {}, New balance: {}", 
                savedTransaction.getId(), wallet.getBalance());
        
        return PaymentResponse.builder()
                .success(true)
                .message("Refund processed successfully")
                .transactionId(savedTransaction.getId())
                .newBalance(wallet.getBalance())
                .build();
    }
    
    /**
     * Get transaction history for a user
     */
    @Transactional(readOnly = true)
    public Page<TransactionDto> getTransactionHistory(Long userId, Pageable pageable) {
        log.debug("Fetching transaction history for user: {}", userId);
        
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        
        Page<Transaction> transactions = transactionRepository.findByWalletId(
                wallet.getId(), pageable);
        
        return transactions.map(walletMapper::toDto);
    }
    
    /**
     * Get transaction by ID
     */
    @Transactional(readOnly = true)
    public TransactionDto getTransaction(Long transactionId) {
        log.debug("Fetching transaction: {}", transactionId);
        
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found: " + transactionId));
        
        return walletMapper.toDto(transaction);
    }
}
