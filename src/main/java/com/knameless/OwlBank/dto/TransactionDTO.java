package com.knameless.OwlBank.dto;

import com.knameless.OwlBank.enums.TransactionType;

import java.time.LocalDate;

public record TransactionDTO(
        Long id,

        TransactionType transactionType,

        double amount,

        Long originAccountId,

        Long destinationAccountId,

        LocalDate transactionDate
) {}
