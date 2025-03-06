package com.knameless.OwlBank.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import com.knameless.OwlBank.enums.AccountType;
import com.knameless.OwlBank.enums.StatusAccount;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @Column(nullable = false, unique = true, length = 10)
    private String number;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAccount state;

    @Column(nullable = false)
    private double balance;

    @Column(nullable = false)
    private boolean gmfExempt;

    @Column(nullable = false, updatable = false)
    private LocalDate creationDate = LocalDate.now();

    private LocalDate modificationDate;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}