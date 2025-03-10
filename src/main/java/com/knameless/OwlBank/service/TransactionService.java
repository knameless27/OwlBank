package com.knameless.OwlBank.service;

import com.knameless.OwlBank.entity.Product;
import com.knameless.OwlBank.entity.Transaction;
import com.knameless.OwlBank.repository.ProductRepository;
import com.knameless.OwlBank.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Transaction createTransaction(Transaction transaction) {
        String transactionType = transaction.getTransactionType().toString().toLowerCase();
        Product originAccount = productRepository.findById(transaction.getOriginAccount().getId())
                .orElseThrow(() -> new RuntimeException("Origin account not found"));

        switch (transactionType) {
            case "consignment":
                originAccount.setBalance(originAccount.getBalance() + transaction.getAmount());
                break;

            case "withdrawal":
                if (originAccount.getBalance() < transaction.getAmount()) {
                    throw new RuntimeException("Insufficient funds");
                }
                originAccount.setBalance(originAccount.getBalance() - transaction.getAmount());
                break;

            case "transfer":
                Product destinationAccount = productRepository.findById(transaction.getDestinationAccount().getId())
                        .orElseThrow(() -> new RuntimeException("Destination account not found"));
                if (originAccount.getBalance() < transaction.getAmount()) {
                    throw new RuntimeException("Insufficient funds for transfer");
                }
                originAccount.setBalance(originAccount.getBalance() - transaction.getAmount());
                destinationAccount.setBalance(destinationAccount.getBalance() + transaction.getAmount());
                productRepository.save(destinationAccount);
                break;

            default:
                throw new RuntimeException("Invalid transaction type");
        }

        productRepository.save(originAccount);
        transaction.setTransactionDate(LocalDate.now());
        return transactionRepository.save(transaction);
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
}