package com.expensetracker.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    private final ExpenseRepository repo;

    public ExpenseController(ExpenseRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Expense> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public Expense addExpense(@RequestBody Expense expense) {
        return repo.save(expense);
    }
}