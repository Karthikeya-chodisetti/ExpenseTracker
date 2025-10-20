package com.expensetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ExpenseService {
    @Autowired
    private ExpenseRepository repo;

    public List<Expense> getAllExpenses() {
        return repo.findAll();
    }

    public Expense addExpense(Expense expense) {
        return repo.save(expense);
    }

    public void deleteExpense(Long id) {
        repo.deleteById(id);
    }

    public List<Expense> getExpensesByCategory(String category) {
        return repo.findByCategory(category);
    }

    public List<Expense> getExpensesByDateRange(LocalDateTime start, LocalDateTime end) {
        return repo.findByDateBetween(start, end);
    }

}
