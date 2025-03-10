package com.knameless.OwlBank.controller;

import com.knameless.OwlBank.controller.TransactionController;
import com.knameless.OwlBank.dto.TransactionDTO;
import com.knameless.OwlBank.enums.TransactionType;
import com.knameless.OwlBank.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private TransactionDTO transactionDTO;

    @BeforeEach
    void setUp() {
        transactionDTO = new TransactionDTO(
                1L,
                TransactionType.DEPOSIT,
                100.0,
                1L,
                2L,
                LocalDate.now()
        );
    }

    @Test
    void createTransaction_ShouldReturnTransactionDTO() {
        when(transactionService.createTransaction(any(TransactionDTO.class))).thenReturn(transactionDTO);

        ResponseEntity<TransactionDTO> response = transactionController.createTransaction(transactionDTO);

        assertNotNull(response.getBody());
        assertEquals(100.0, response.getBody().amount());
        assertEquals(TransactionType.DEPOSIT, response.getBody().transactionType());
        verify(transactionService, times(1)).createTransaction(any(TransactionDTO.class));
    }

    @Test
    void getTransactionById_ShouldReturnTransactionDTO_WhenFound() {
        when(transactionService.getTransactionById(anyLong())).thenReturn(transactionDTO);

        ResponseEntity<TransactionDTO> response = transactionController.getTransactionById(1L);

        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().id());
        assertEquals(TransactionType.DEPOSIT, response.getBody().transactionType());
        verify(transactionService, times(1)).getTransactionById(1L);
    }
}
