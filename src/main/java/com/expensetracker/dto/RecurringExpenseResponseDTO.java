package com.expensetracker.dto;

import java.time.LocalDateTime;

public class RecurringExpenseResponseDTO {

    private Long id;
    private String title;
    private double amount;
    private String category;
    private String note;
    private String tags;
    private String frequency;
    private LocalDateTime nextDate;

    public RecurringExpenseResponseDTO() {
    }

    public RecurringExpenseResponseDTO(
            Long id,
            String title,
            double amount,
            String category,
            String note,
            String tags,
            String frequency,
            LocalDateTime nextDate) {

        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.note = note;
        this.tags = tags;
        this.frequency = frequency;
        this.nextDate = nextDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public LocalDateTime getNextDate() {
        return nextDate;
    }

    public void setNextDate(LocalDateTime nextDate) {
        this.nextDate = nextDate;
    }
}