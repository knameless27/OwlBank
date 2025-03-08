package com.knameless.OwlBank.dto;

import com.knameless.OwlBank.enums.AccountType;
import com.knameless.OwlBank.enums.StatusAccount;

import java.time.LocalDate;

public record ProductDTO(
        Long id,

        AccountType type,

        String number,

        StatusAccount status,

        double balance,

        boolean gmfExempt,

        Long clientId,

        LocalDate creationDate
) {}
