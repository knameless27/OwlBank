package com.knameless.OwlBank.service;

import com.knameless.OwlBank.dto.TransactionDTO;
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
    public TransactionDTO createTransaction(TransactionDTO transactionDTO) {
        String transactionType = transactionDTO.transactionType().toString().toLowerCase();

        Product originAccount = productRepository.findById(transactionDTO.originAccountId())
                .orElseThrow(() -> new RuntimeException("Origin account not found"));

        Product destinationAccount = null;

        switch (transactionType) {
            case "deposit":
                makeDeposit(originAccount, transactionDTO);
                break;

            case "withdrawal":
                makeWithdrawal(originAccount, transactionDTO);
                break;

            case "transfer":
                destinationAccount = makeTransfer(originAccount, transactionDTO);
                break;

            default:
                throw new RuntimeException("Invalid transaction type");
        }

        productRepository.save(originAccount);

        Transaction transaction = new Transaction();
        transaction.setTransactionType(transactionDTO.transactionType());
        transaction.setAmount(transactionDTO.amount());
        transaction.setOriginAccount(originAccount);
        transaction.setDestinationAccount(destinationAccount);

        transaction = transactionRepository.save(transaction);

        return new TransactionDTO(
                transaction.getId(),
                transaction.getTransactionType(),
                transaction.getAmount(),
                transaction.getOriginAccount().getId(),
                transaction.getDestinationAccount() != null ? transaction.getDestinationAccount().getId() : null,
                transaction.getTransactionDate()
        );
    }

    private void makeDeposit(Product originAccount, TransactionDTO transactionDTO) {
        originAccount.setBalance(originAccount.getBalance() + transactionDTO.amount());
    }

    private void makeWithdrawal(Product originAccount, TransactionDTO transactionDTO) {
        if (originAccount.getBalance() < transactionDTO.amount())
            throw new RuntimeException("Insufficient funds");

        originAccount.setBalance(originAccount.getBalance() - transactionDTO.amount());
    }

    private Product makeTransfer(Product originAccount, TransactionDTO transactionDTO) {
        Product destinationAccount = null;

        destinationAccount = productRepository.findById(transactionDTO.destinationAccountId())
                .orElseThrow(() -> new RuntimeException("Destination account not found"));

        if (originAccount.getBalance() < transactionDTO.amount())
            throw new RuntimeException("Insufficient funds for transfer");

        originAccount.setBalance(originAccount.getBalance() - transactionDTO.amount());
        destinationAccount.setBalance(destinationAccount.getBalance() + transactionDTO.amount());

        return productRepository.save(destinationAccount);
    }

    public TransactionDTO getTransactionById(Long id) {
        return transactionRepository.findById(id).map(transaction ->
                new TransactionDTO(
                        transaction.getId(),
                        transaction.getTransactionType(),
                        transaction.getAmount(),
                        transaction.getOriginAccount() != null ? transaction.getOriginAccount().getId() : null,
                        transaction.getDestinationAccount() != null ? transaction.getDestinationAccount().getId() : null,
                        transaction.getTransactionDate()
                )
        ).orElseThrow(() -> new RuntimeException("Transaction not found!"));
    }

}