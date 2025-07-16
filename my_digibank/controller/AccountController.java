package com.mydigibank.my_digibank.controller;

import com.mydigibank.my_digibank.model.Account;
import com.mydigibank.my_digibank.model.Transaction;
import com.mydigibank.my_digibank.service.AccountService;
import com.mydigibank.my_digibank.dto.AccountRequestDto;
import com.mydigibank.my_digibank.dto.TransferRequestDto;

import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    // ✅ Create account with input validation
    @PostMapping
    public ResponseEntity<Account> create(@Valid @RequestBody AccountRequestDto acc) {
        return ResponseEntity.ok(service.createAccountFromDto(acc));
    }

    // ✅ Get account by ID
    @GetMapping("/{id}")
    public ResponseEntity<Account> get(@PathVariable String id) {
        return service.getAccountById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ Get accounts by type and/or status
    @GetMapping
    public ResponseEntity<List<Account>> search(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(service.searchAccounts(type, status));
    }
    @PutMapping("/{id}/close")
    public ResponseEntity<String> closeAccount(@PathVariable String id) {
        service.closeAccount(id);
        return ResponseEntity.ok("Account closed successfully.");
    }
    @PutMapping("/test-close")
    public ResponseEntity<String> testClose() {
        return ResponseEntity.ok("PUT close endpoint working");
    }

    // ✅ Update account
    @PutMapping("/{id}")
    public ResponseEntity<Account> update(@PathVariable String id, @RequestBody Account acc) {
        return ResponseEntity.ok(service.updateAccount(id, acc));
    }

    // ✅ Transfer money between accounts
    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody TransferRequestDto dto) {
        service.transferAmount(dto);
        return ResponseEntity.ok("Transfer successful");
    }

    // ✅ Get transactions for an account with optional filters
    @GetMapping("/{id}/transactions")
    public ResponseEntity<List<Transaction>> getTransactions(
            @PathVariable String id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Double minAmt,
            @RequestParam(required = false) Double maxAmt,
            @RequestParam(required = false) String type  // "Credit" or "Debit"
    ) {
        List<Transaction> txns = service.getTransactions(id, fromDate, toDate, minAmt, maxAmt, type);
        return ResponseEntity.ok(txns);
    }
    
    
    
}

