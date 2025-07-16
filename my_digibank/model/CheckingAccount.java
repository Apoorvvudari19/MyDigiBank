package com.mydigibank.my_digibank.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Checking")
public class CheckingAccount extends Account {

    // Default overdraft limit set to ₹50,000
    private double overdraftLimit = 50000.0;

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
}
