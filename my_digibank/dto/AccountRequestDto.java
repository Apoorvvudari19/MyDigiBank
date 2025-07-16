package com.mydigibank.my_digibank.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AccountRequestDto {

    @NotBlank(message = "Account holder name must not be blank")
    public String accountHolderName;

    @NotBlank(message = "Account type must not be blank")
    public String accountType;  // "Savings" or "Checking"

    public Double balance;  // optional; defaulted to 0.0 in service

    @Email(message = "Invalid email format")
    public String email;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid phone number")
    public String phoneNumber;

    public String ifsc;    // optional; defaulted to "HDFC0003949" in service

    public String status;  // optional; defaulted to "ACTIVE" in service
}
