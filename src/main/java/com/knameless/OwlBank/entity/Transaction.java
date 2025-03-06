package com.knameless.OwlBank.entity;

import com.knameless.OwlBank.enums.TransactionType;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false)
    private double amount;

    @ManyToOne
    @JoinColumn(name = "origin_account_id", nullable = true)
    private Product originAccount;

    @ManyToOne
    @JoinColumn(name = "destination_account_id", nullable = true)
    private Product destinationAccount;

    @Column(nullable = false, updatable = false)
    private LocalDate transactionDate = LocalDate.now();
}
