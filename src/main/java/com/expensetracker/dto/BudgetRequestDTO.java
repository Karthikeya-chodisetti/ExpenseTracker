package com.expensetracker.dto;

public class BudgetRequestDTO {

    private String category;
    private double limitAmount;

    public BudgetRequestDTO() {
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }
}