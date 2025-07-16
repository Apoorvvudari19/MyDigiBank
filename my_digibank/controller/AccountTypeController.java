package com.mydigibank.my_digibank.controller;

import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/accounts/types")
public class AccountTypeController {

    @GetMapping
    public List<Map<String, Object>> getTypes() {
        List<Map<String, Object>> types = new ArrayList<>();

        Map<String, Object> savings = new HashMap<>();
        savings.put("type", "Savings");
        savings.put("interestRate", 3.0);

        Map<String, Object> checking = new HashMap<>();
        checking.put("type", "Checking");
        checking.put("overdraftLimit", 50000.00);

        types.add(savings);
        types.add(checking);

        return types;
    }
    
}
   

