package com.mydigibank.my_digibank.controller;

import com.mydigibank.my_digibank.dto.LoginRequestDto;
import com.mydigibank.my_digibank.repository.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final AccountRepository accountRepo;

    public UserController(AccountRepository accountRepo) {
        this.accountRepo = accountRepo;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDto loginDto) {
        boolean exists = accountRepo.existsById(loginDto.accountId);

        if (!exists) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("❌ Account does not exist");
        }

        return ResponseEntity.ok("✅ Login successful");
    }
}

