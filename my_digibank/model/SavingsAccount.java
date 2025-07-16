package com.mydigibank.my_digibank.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("Savings")
public class SavingsAccount extends Account {

    // Default interest rate set to 3.0%
    private double interestRate = 3.0;

    public double getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
}
