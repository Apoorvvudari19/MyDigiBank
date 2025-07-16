package com.mydigibank.my_digibank.service;

import com.mydigibank.my_digibank.model.*;
import com.mydigibank.my_digibank.repository.AccountRepository;
import com.mydigibank.my_digibank.repository.TransactionRepository;
import com.mydigibank.my_digibank.dto.AccountRequestDto;
import com.mydigibank.my_digibank.dto.TransferRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.*;
import java.util.*;

@Service
public class AccountService {

    private final AccountRepository repo;
    private final TransactionRepository transactionRepo;
    private final ExchangeRateService exchangeRateService;

    public AccountService(AccountRepository repo,
                          TransactionRepository transactionRepo,
                          ExchangeRateService exchangeRateService) {
        this.repo = repo;
        this.transactionRepo = transactionRepo;
        this.exchangeRateService = exchangeRateService;
    }

    public Account createAccount(Account acc) {
        acc.setDateOfCreation(LocalDate.now());
        acc.setAccountId(generateAccountId());
        return repo.save(acc);
    }

    private String generateAccountId() {
        long number = (long) (Math.random() * 9_000_000_000_000L) + 1_000_000_000_000L;
        return String.valueOf(number).substring(0, 12);
    }

    public Optional<Account> getAccountById(String id) {
        return repo.findById(id);
    }

    public List<Account> searchAccounts(String type, String status) {
        if (type == null && status == null) {
            return repo.findAll();
        } else if (type != null && status != null) {
            return repo.findByAccountTypeAndStatus(type, status);
        } else {
            return repo.findByStatus(status);
        }
    }

    public Account updateAccount(String id, Account updated) {
        return repo.findById(id)
                .map(acc -> {
                    acc.setAccountHolderName(updated.getAccountHolderName());
                    acc.setEmail(updated.getEmail());
                    acc.setPhoneNumber(updated.getPhoneNumber());
                    acc.setIfsc(updated.getIfsc());
                    acc.setAccountType(updated.getAccountType());
                    return repo.save(acc);
                })
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public void closeAccount(String id) {
        repo.findById(id).ifPresentOrElse(acc -> {
            acc.setStatus("Closed");
            repo.save(acc);
        }, () -> {
            throw new RuntimeException("Account not found.");
        });
    }

    public Account debit(String accountId, double amount) {
        return repo.findById(accountId).map(acc -> {
            if ("Closed".equalsIgnoreCase(acc.getStatus())) {
                throw new RuntimeException("Account is closed and cannot be debited");
            }
            if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
            if (acc.getBalance() < amount) throw new RuntimeException("Insufficient funds");
            acc.setBalance(acc.getBalance() - amount);
            return repo.save(acc);
        }).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Account credit(String accountId, double amount) {
        return repo.findById(accountId).map(acc -> {
            if ("Closed".equalsIgnoreCase(acc.getStatus())) {
                throw new RuntimeException("Account is closed and cannot be credited");
            }
            if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");
            acc.setBalance(acc.getBalance() + amount);
            return repo.save(acc);
        }).orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Account createAccountFromDto(AccountRequestDto dto) {
        if (dto.accountHolderName == null || dto.accountHolderName.isBlank()) {
            throw new IllegalArgumentException("Account holder name must not be blank");
        }

        if (dto.accountType == null || dto.accountType.isBlank()) {
            throw new IllegalArgumentException("Account type must not be blank");
        }

        if (dto.status == null || dto.status.isBlank()) {
            dto.status = "ACTIVE";
        }

        // 🔒 Duplicate prevention check
        List<Account> duplicates = repo.findByAccountHolderNameAndEmailAndPhoneNumberAndAccountType(
                dto.accountHolderName,
                dto.email,
                dto.phoneNumber,
                dto.accountType
            );

        if (!duplicates.isEmpty()) {
            throw new IllegalArgumentException(
                "Duplicate account detected. An account already exists with the same " +
                "name, email, phone number, and account type. Please change at least one of these."
            );
        }

        Account acc;

        if ("Savings".equalsIgnoreCase(dto.accountType)) {
            SavingsAccount sa = new SavingsAccount();
            sa.setInterestRate(3.0);
            acc = sa;
        } else if ("Checking".equalsIgnoreCase(dto.accountType)) {
            CheckingAccount ca = new CheckingAccount();
            ca.setOverdraftLimit(50000.0);
            acc = ca;
        } else {
            throw new IllegalArgumentException("Invalid account type: " + dto.accountType);
        }

        acc.setAccountId(generateAccountId());
        acc.setDateOfCreation(LocalDate.now());
        acc.setAccountHolderName(dto.accountHolderName);
        acc.setAccountType(dto.accountType);
        acc.setBalance(dto.balance != null ? dto.balance : 0.0);
        acc.setEmail(dto.email);
        acc.setPhoneNumber(dto.phoneNumber);
        acc.setIfsc(dto.ifsc != null ? dto.ifsc : "HDFC0003949");
        acc.setStatus(dto.status);

        return repo.save(acc);
    }


    @Transactional
    public void transferAmount(TransferRequestDto dto) {
        if (dto.amount <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Transfer amount must be positive");
        }

        Account from = repo.findById(dto.fromAccountId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Source account not found"));

        Account to = repo.findById(dto.toAccountId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination account not found"));

        if ("Closed".equalsIgnoreCase(from.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Source account is closed");
        }

        if ("Closed".equalsIgnoreCase(to.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Destination account is closed");
        }

        // Convert from source currency to INR for balance deduction
        double debitInINR = dto.fromCurrency.equalsIgnoreCase("INR")
            ? dto.amount
            : exchangeRateService.convert(dto.fromCurrency, "INR", dto.amount);

        if (from.getBalance() < debitInINR) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Insufficient INR-equivalent balance");
        }

        // Convert INR to target currency for credit
        double creditAmountInTargetCurrency = dto.toCurrency.equalsIgnoreCase("INR")
            ? debitInINR
            : exchangeRateService.convert("INR", dto.toCurrency, debitInINR);

        // Update balances (store internally in INR)
        from.setBalance(from.getBalance() - debitInINR);
        to.setBalance(to.getBalance() + debitInINR);

        repo.save(from);
        repo.save(to);

        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));

        // Debit Transaction in source currency
        Transaction debit = new Transaction();
        debit.setTransactionId("TXN" + UUID.randomUUID().toString().substring(0, 8));
        debit.setAccountId(from.getAccountId());
        debit.setTransactionType("Debit");
        debit.setAmount(dto.amount);
        debit.setCurrency(dto.fromCurrency);
        debit.setCurrencyCode(dto.fromCurrency);
        debit.setOriginalAmount(dto.amount);
        debit.setConvertedAmount(debitInINR);
        debit.setDescription("Transfer to " + to.getAccountId() + ": " + dto.description + " (~₹" + String.format("%.2f", debitInINR) + ")");
        debit.setDateOfTransaction(now);

        // Credit Transaction in target currency
     // Step 5: Record Credit Transaction in Target Currency
        Transaction credit = new Transaction();
        credit.setTransactionId("TXN" + UUID.randomUUID().toString().substring(0, 8));
        credit.setAccountId(to.getAccountId());
        credit.setTransactionType("Credit");
        credit.setAmount(creditAmountInTargetCurrency);
        credit.setCurrency(dto.toCurrency); // ✅ correct currency
        credit.setCurrencyCode(dto.toCurrency); // ✅ correct currencyCode
        credit.setOriginalAmount(dto.amount); // ✅ record original sent amount
        credit.setConvertedAmount(creditAmountInTargetCurrency); // ✅ record received amount

        credit.setDescription(
            "Received from " + from.getAccountId() + ": " + dto.description +
            " (~" + dto.amount + " " + dto.fromCurrency + ")"
        );
        credit.setDateOfTransaction(now);


        transactionRepo.save(debit);
        transactionRepo.save(credit);

        System.out.printf("Transferred %.2f %s (%.2f INR) from %s → credited %.2f %s to %s%n",
            dto.amount, dto.fromCurrency, debitInINR,
            from.getAccountId(), creditAmountInTargetCurrency, dto.toCurrency, to.getAccountId());
    }




    public List<Transaction> getTransactions(
            String accountId,
            LocalDate fromDate,
            LocalDate toDate,
            Double minAmt,
            Double maxAmt,
            String type
    ) {
        List<Transaction> txns = transactionRepo.findByAccountId(accountId);

        return txns.stream()
                .filter(txn -> fromDate == null || !txn.getDateOfTransaction().toLocalDate().isBefore(fromDate))
                .filter(txn -> toDate == null || !txn.getDateOfTransaction().toLocalDate().isAfter(toDate))
                .filter(txn -> minAmt == null || txn.getAmount() >= minAmt)
                .filter(txn -> maxAmt == null || txn.getAmount() <= maxAmt)
                .filter(txn -> type == null || txn.getTransactionType().equalsIgnoreCase(type))
                .toList();
    }
}
