package com.velocity.payment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import com.velocity.payment.model.dto.TransactionDto;
import com.velocity.payment.model.dto.WalletDto;
import com.velocity.payment.model.entity.Transaction;
import com.velocity.payment.model.entity.Wallet;

/**
 * MapStruct mapper for Wallet and Transaction entities.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WalletMapper {
    
    /**
     * Convert Wallet entity to DTO
     */
    WalletDto toDto(Wallet wallet);
    
    /**
     * Convert Transaction entity to DTO
     */
    TransactionDto toDto(Transaction transaction);
}
