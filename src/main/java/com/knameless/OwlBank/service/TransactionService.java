package com.knameless.OwlBank.service;

import com.knameless.OwlBank.entity.Product;
import com.knameless.OwlBank.entity.Transaction;
import com.knameless.OwlBank.repository.ProductRepository;
import com.knameless.OwlBank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        Product originAccount = productRepository.findById(transaction.getOriginAccount().getId())
                .orElseThrow(() -> new RuntimeException("Origin account not found"));

        if (transaction.getTransactionType().equals("withdrawal") || transaction.getTransactionType().equals("transfer")) {
            if (originAccount.getBalance() < transaction.getAmount()) {
                throw new RuntimeException("Insufficient funds");
            }
            originAccount.setBalance(originAccount.getBalance() - transaction.getAmount());
        }

        if (transaction.getTransactionType().equals("deposit") || transaction.getTransactionType().equals("transfer")) {
            Product destinationAccount = productRepository.findById(transaction.getDestinationAccount().getId())
                    .orElseThrow(() -> new RuntimeException("Destination account not found"));
            destinationAccount.setBalance(destinationAccount.getBalance() + transaction.getAmount());
            productRepository.save(destinationAccount);
        }

        productRepository.save(originAccount);
        transaction.setTransactionDate(LocalDate.now());
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getTransactions() {
        return transactionRepository.findAll();
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
}
