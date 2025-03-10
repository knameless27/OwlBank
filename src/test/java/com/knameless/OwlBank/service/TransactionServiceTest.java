package com.knameless.OwlBank.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.knameless.OwlBank.dto.TransactionDTO;
import com.knameless.OwlBank.entity.Product;
import com.knameless.OwlBank.enums.StatusAccount;
import com.knameless.OwlBank.enums.TransactionType;
import com.knameless.OwlBank.repository.ProductRepository;
import com.knameless.OwlBank.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Product originAccount;
    private Product destinationAccount;

    @BeforeEach
    void setUp() {
        originAccount = new Product();
        originAccount.setId(1L);
        originAccount.setBalance(1000.0);
        originAccount.setStatus(StatusAccount.ACTIVE);

        destinationAccount = new Product();
        destinationAccount.setId(2L);
        destinationAccount.setBalance(500.0);
        destinationAccount.setStatus(StatusAccount.ACTIVE);
    }

    @Test
    void testDeposit() {
        TransactionDTO dto = new TransactionDTO(null, TransactionType.DEPOSIT, 200.0, 1L, null, LocalDate.now());
        when(productRepository.findById(1L)).thenReturn(Optional.of(originAccount));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionDTO result = transactionService.createTransaction(dto);

        assertEquals(1200.0, originAccount.getBalance());
        assertEquals(TransactionType.DEPOSIT, result.transactionType());
    }

    @Test
    void testWithdrawalWithSufficientFunds() {
        TransactionDTO dto = new TransactionDTO(null, TransactionType.WITHDRAWAL, 500.0, 1L, null, LocalDate.now());
        when(productRepository.findById(1L)).thenReturn(Optional.of(originAccount));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionDTO result = transactionService.createTransaction(dto);

        assertEquals(500.0, originAccount.getBalance());
        assertEquals(TransactionType.WITHDRAWAL, result.transactionType());
    }

    @Test
    void testWithdrawalWithInsufficientFunds() {
        TransactionDTO dto = new TransactionDTO(null, TransactionType.WITHDRAWAL, 2000.0, 1L, null, LocalDate.now());
        when(productRepository.findById(1L)).thenReturn(Optional.of(originAccount));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> transactionService.createTransaction(dto));
        assertEquals("Insufficient funds", exception.getMessage());
    }

    @Test
    void testTransferWithSufficientFunds() {
        TransactionDTO dto = new TransactionDTO(null, TransactionType.TRANSFER, 300.0, 1L, 2L, LocalDate.now());
        when(productRepository.findById(1L)).thenReturn(Optional.of(originAccount));
        when(productRepository.findById(2L)).thenReturn(Optional.of(destinationAccount));
        when(transactionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionDTO result = transactionService.createTransaction(dto);

        assertEquals(700.0, originAccount.getBalance());
        assertEquals(800.0, destinationAccount.getBalance());
        assertEquals(TransactionType.TRANSFER, result.transactionType());
    }

    @Test
    void testTransferWithInsufficientFunds() {
        TransactionDTO dto = new TransactionDTO(null, TransactionType.TRANSFER, 2000.0, 1L, 2L, LocalDate.now());
        when(productRepository.findById(1L)).thenReturn(Optional.of(originAccount));
        when(productRepository.findById(2L)).thenReturn(Optional.of(destinationAccount));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> transactionService.createTransaction(dto));
        assertEquals("Insufficient funds for transfer", exception.getMessage());
    }

    @Test
    void testTransferToNonExistingAccount() {
        TransactionDTO dto = new TransactionDTO(null, TransactionType.TRANSFER, 300.0, 1L, 99L, LocalDate.now());
        when(productRepository.findById(1L)).thenReturn(Optional.of(originAccount));
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> transactionService.createTransaction(dto));
        assertEquals("Destination account not found", exception.getMessage());
    }
}