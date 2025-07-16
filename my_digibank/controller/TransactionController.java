package com.mydigibank.my_digibank.controller;

import com.mydigibank.my_digibank.model.Transaction;
import com.mydigibank.my_digibank.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // ✅ POST /transactions
    @PostMapping
    public ResponseEntity<Transaction> recordTransaction(@RequestBody Transaction tx) {
        return ResponseEntity.ok(transactionService.recordTransaction(tx));
    }

    // ✅ GET /transactions/account/{accountId}
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Transaction>> getTransactionHistory(@PathVariable String accountId) {
        return ResponseEntity.ok(transactionService.getTransactionHistory(accountId));
    }

    // ✅ GET /transactions/filter
    @GetMapping("/filter")
    public ResponseEntity<List<Transaction>> filterTransactions(
            @RequestParam String accountId,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount
    ) {
        List<Transaction> results = transactionService.filterTransactions(accountId, type, fromDate, toDate, minAmount, maxAmount);
        return ResponseEntity.ok(results);
    }
}
