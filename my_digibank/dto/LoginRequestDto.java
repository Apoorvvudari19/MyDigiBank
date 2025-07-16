package com.mydigibank.my_digibank.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDto {

    @NotBlank(message = "Account ID must not be blank")
    public String accountId;

    // Constructor
    public LoginRequestDto() {
    }

    public LoginRequestDto(String accountId) {
        this.accountId = accountId;
    }

    // Getter
    public String getAccountId() {
        return accountId;
    }

    // Setter
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
}
