package com.velocity.payment.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.velocity.core.dto.ApiResponse;
import com.velocity.payment.model.dto.AddMoneyRequest;
import com.velocity.payment.model.dto.DeductMoneyRequest;
import com.velocity.payment.model.dto.PaymentResponse;
import com.velocity.payment.model.dto.RefundRequest;
import com.velocity.payment.model.dto.TransactionDto;
import com.velocity.payment.model.dto.WalletDto;
import com.velocity.payment.service.WalletService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST Controller for wallet and payment operations.
 * Provides endpoints for wallet management, transactions, and payment processing.
 */
@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Wallet Management", description = "APIs for wallet and payment operations")
public class WalletController {
    
    private final WalletService walletService;
    
    /**
     * Create a new wallet for a user
     */
    @PostMapping("/create")
    @Operation(summary = "Create wallet", description = "Create a new wallet for a user")
    public ResponseEntity<ApiResponse<WalletDto>> createWallet(@RequestParam Long userId) {
        log.info("Request to create wallet for user: {}", userId);
        
        WalletDto wallet = walletService.createWallet(userId);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(wallet, "Wallet created successfully"));
    }
    
    /**
     * Get wallet by user ID
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "Get wallet", description = "Get wallet details by user ID")
    public ResponseEntity<ApiResponse<WalletDto>> getWallet(@PathVariable Long userId) {
        log.info("Request to get wallet for user: {}", userId);
        
        WalletDto wallet = walletService.getWalletByUserId(userId);
        
        return ResponseEntity.ok(ApiResponse.success(wallet, "Wallet retrieved successfully"));
    }
    
    /**
     * Get wallet balance
     */
    @GetMapping("/balance/{userId}")
    @Operation(summary = "Get balance", description = "Get wallet balance for a user")
    public ResponseEntity<ApiResponse<BigDecimal>> getBalance(@PathVariable Long userId) {
        log.info("Request to get balance for user: {}", userId);
        
        BigDecimal balance = walletService.getBalance(userId);
        
        return ResponseEntity.ok(ApiResponse.success(balance, "Balance retrieved successfully"));
    }
    
    /**
     * Add money to wallet
     */
    @PostMapping("/add-money/{userId}")
    @Operation(summary = "Add money", description = "Add money to wallet")
    public ResponseEntity<ApiResponse<PaymentResponse>> addMoney(
            @PathVariable Long userId,
            @Valid @RequestBody AddMoneyRequest request) {
        log.info("Request to add money to wallet for user: {}, amount: {}", userId, request.getAmount());
        
        PaymentResponse response = walletService.addMoney(userId, request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Money added successfully"));
    }
    
    /**
     * Deduct money from wallet (internal API for services)
     */
    @PostMapping("/deduct")
    @Operation(summary = "Deduct money", description = "Deduct money from wallet (internal use)")
    public ResponseEntity<ApiResponse<PaymentResponse>> deductMoney(
            @Valid @RequestBody DeductMoneyRequest request) {
        log.info("Request to deduct money from wallet for user: {}, amount: {}", 
                request.getUserId(), request.getAmount());
        
        PaymentResponse response = walletService.deductMoney(request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Payment processed successfully"));
    }
    
    /**
     * Refund money to wallet
     */
    @PostMapping("/refund")
    @Operation(summary = "Refund money", description = "Refund money to wallet")
    public ResponseEntity<ApiResponse<PaymentResponse>> refundMoney(
            @Valid @RequestBody RefundRequest request) {
        log.info("Request to refund money to wallet for user: {}, amount: {}", 
                request.getUserId(), request.getAmount());
        
        PaymentResponse response = walletService.refundMoney(request);
        
        return ResponseEntity.ok(ApiResponse.success(response, "Refund processed successfully"));
    }
    
    /**
     * Get transaction history
     */
    @GetMapping("/transactions/{userId}")
    @Operation(summary = "Get transactions", description = "Get transaction history for a user")
    public ResponseEntity<ApiResponse<Page<TransactionDto>>> getTransactionHistory(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("Request to get transaction history for user: {}, page: {}, size: {}", userId, page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<TransactionDto> transactions = walletService.getTransactionHistory(userId, pageable);
        
        return ResponseEntity.ok(ApiResponse.success(transactions, "Transaction history retrieved successfully"));
    }
    
    /**
     * Get transaction by ID
     */
    @GetMapping("/transactions/detail/{transactionId}")
    @Operation(summary = "Get transaction", description = "Get transaction details by ID")
    public ResponseEntity<ApiResponse<TransactionDto>> getTransaction(@PathVariable Long transactionId) {
        log.info("Request to get transaction: {}", transactionId);
        
        TransactionDto transaction = walletService.getTransaction(transactionId);
        
        return ResponseEntity.ok(ApiResponse.success(transaction, "Transaction retrieved successfully"));
    }
}
