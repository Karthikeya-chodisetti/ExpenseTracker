package com.expensetracker.dto;

public class BudgetResponseDTO {

    private Long id;
    private String category;
    private double limitAmount;

    public BudgetResponseDTO() {
    }

    public BudgetResponseDTO(Long id, String category, double limitAmount) {
        this.id = id;
        this.category = category;
        this.limitAmount = limitAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
