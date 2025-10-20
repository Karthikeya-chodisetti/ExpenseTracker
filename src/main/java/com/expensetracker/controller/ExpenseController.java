package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService service;

    @PostMapping
    public Expense addExpense(@RequestBody Expense expense) {
        return service.addExpense(expense);
    }

    @DeleteMapping("/{id}")
    public void deleteExpense(@PathVariable Long id) {
        service.deleteExpense(id);
    }

    @GetMapping("/test")
    public String test() {
        return "Expense Tracker API Working!!";
    }

    @GetMapping
    public List<Expense> getExpenses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {
        if (category != null) {
            return service.getExpensesByCategory(category);
        } else if (start != null && end != null) {
            LocalDateTime startDate = LocalDateTime.parse(start + "T00:00:00");
            LocalDateTime endDate = LocalDateTime.parse(end + "T23:59:59");
            return service.getExpensesByDateRange(startDate, endDate);
        } else
            return service.getAllExpenses();
    }
}
