package com.mydigibank.my_digibank.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.ZonedDateTime;

@Entity
public class Transaction {

    @Id
    private String transactionId;

    @NotBlank(message = "Account ID must not be blank")
    private String accountId;

    @NotBlank(message = "Transaction type must not be blank")
    private String transactionType;

    @Positive(message = "Amount must be greater than zero")
    private double amount;

    @Column(nullable = false)
    private String currencyCode;

    @Column(nullable = false)
    private String currency;

    private Double originalAmount; // original amount sent (e.g., INR 1000)
    private Double convertedAmount; // converted amount received (e.g., USD 11.96)

    @Column(name = "date_of_transaction")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX", timezone = "Asia/Kolkata")
    private ZonedDateTime dateOfTransaction;

    @NotBlank(message = "Description must not be blank")
    private String description;

    // Getters & Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public ZonedDateTime getDateOfTransaction() { return dateOfTransaction; }
    public void setDateOfTransaction(ZonedDateTime dateOfTransaction) { this.dateOfTransaction = dateOfTransaction; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Double getOriginalAmount() { return originalAmount; }
    public void setOriginalAmount(Double originalAmount) { this.originalAmount = originalAmount; }

    public Double getConvertedAmount() { return convertedAmount; }
    public void setConvertedAmount(Double convertedAmount) { this.convertedAmount = convertedAmount; }
}
