package com.mydigibank.my_digibank.dto;

import jakarta.validation.constraints.*;

public class TransferRequestDto {

    @NotBlank(message = "From Account ID is required")
    public String fromAccountId;

    @NotBlank(message = "To Account ID is required")
    public String toAccountId;

    @Positive(message = "Amount must be positive")
    public double amount;

    @NotBlank(message = "From Currency is required")
    public String fromCurrency;

    @NotBlank(message = "To Currency is required")
    public String toCurrency;

    @NotBlank(message = "Description is required")
    public String description;

   
}
