package com.expensetracker.dto;

import java.time.LocalDateTime;

public class ExpenseResponseDTO {

    private Long id;
    private double amount;
    private String category;
    private String title;
    private String note;
    private String tags;
    private LocalDateTime date;

    public ExpenseResponseDTO() {
    }

    public ExpenseResponseDTO(Long id, double amount, String category, String title, String note, String tags,
            LocalDateTime date) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.title = title;
        this.note = note;
        this.tags = tags;
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}