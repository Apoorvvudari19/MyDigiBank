package com.mydigibank.my_digibank.service;

import com.mydigibank.my_digibank.model.Account;
import com.mydigibank.my_digibank.model.Transaction;
import com.mydigibank.my_digibank.repository.AccountRepository;
import com.mydigibank.my_digibank.repository.TransactionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepo;
    private final AccountRepository accountRepo;

    public TransactionService(TransactionRepository transactionRepo, AccountRepository accountRepo) {
        this.transactionRepo = transactionRepo;
        this.accountRepo = accountRepo;
    }

    @Transactional
    public Transaction recordTransaction(Transaction tx) {
        if (tx.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        Account acc = accountRepo.findById(tx.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if ("Debit".equalsIgnoreCase(tx.getTransactionType())) {
            if (acc.getBalance() < tx.getAmount()) {
                throw new RuntimeException("Insufficient balance");
            }
            acc.setBalance(acc.getBalance() - tx.getAmount());
        } else if ("Credit".equalsIgnoreCase(tx.getTransactionType())) {
            acc.setBalance(acc.getBalance() + tx.getAmount());
        } else {
            throw new IllegalArgumentException("Transaction type must be Debit or Credit");
        }

        tx.setTransactionId(generateTransactionId());

        // ✅ Set timestamp in Asia/Kolkata timezone
        tx.setDateOfTransaction(ZonedDateTime.now(ZoneId.of("Asia/Kolkata")));

        accountRepo.save(acc);
        return transactionRepo.save(tx);
    }

    public List<Transaction> getTransactionHistory(String accountId) {
        return transactionRepo.findByAccountId(accountId);
    }

    public List<Transaction> filterTransactions(
            String accountId,
            String type,
            LocalDate fromDate,
            LocalDate toDate,
            Double minAmount,
            Double maxAmount
    ) {
        List<Transaction> txns = transactionRepo.findByAccountId(accountId);

        return txns.stream()
                .filter(t -> type == null || t.getTransactionType().equalsIgnoreCase(type))
                .filter(t -> fromDate == null || !t.getDateOfTransaction().toLocalDate().isBefore(fromDate))
                .filter(t -> toDate == null || !t.getDateOfTransaction().toLocalDate().isAfter(toDate))
                .filter(t -> minAmount == null || t.getAmount() >= minAmount)
                .filter(t -> maxAmount == null || t.getAmount() <= maxAmount)
                .toList();
    }

    private String generateTransactionId() {
        return "TXN" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
