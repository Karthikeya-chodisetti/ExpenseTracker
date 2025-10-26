package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

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
            @RequestParam(required = false) String end,
            @RequestParam(required = false) Double minAmount,
            @RequestParam(required = false) Double maxAmount) {

        LocalDateTime startDate = start != null ? LocalDateTime.parse(start + "T00:00:00") : null;
        LocalDateTime endDate = end != null ? LocalDateTime.parse(end + "T23:59:59") : null;

        return service.getFilteredExpenses(category, startDate, endDate, minAmount, maxAmount);
    }

    @GetMapping("/summary")
    public Map<String, Object> getSpendingSummary(
            @RequestParam String period,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {

        return service.getSpendingSummary(period, category, start, end);
    }

    @GetMapping("/summary/categories")
    public Map<String, Object> getCategoryWiseSummary(
            @RequestParam String period,
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end) {

        return service.getCategoryWiseSummary(period, start, end);
    }

    @GetMapping("/summary/daily")
    public Map<String, Double> getDailySummary(
            @RequestParam String start,
            @RequestParam String end) {

        return service.getDailySummary(start, end);
    }

    @PutMapping("/{id}")
    public Expense updateExpense(@PathVariable Long id, @RequestBody Expense expense) {
        return service.updateExpense(id, expense);
    }

    @GetMapping("/recurring")
    public List<Expense> getRecurringExpenses() {
        return service.getRecurringExpenses();
    }

    @PutMapping("/recurring/{id}/activate")
    public Expense activateRecurring(@PathVariable Long id) {
        return service.setRecurringStatus(id, true);
    }

    @PutMapping("/recurring/{id}/deactivate")
    public Expense deactivateRecurring(@PathVariable Long id) {
        return service.setRecurringStatus(id, false);
    }

    @GetMapping("/search")
    public List<Expense> searchExpenses(@RequestParam String keyword) {
        return service.searchExpenses(keyword);
    }

    @GetMapping("/sorted")
    public List<Expense> getSortedExpenses(
            @RequestParam(defaultValue = "date") String sortBy,
            @RequestParam(defaultValue = "desc") String order) {
        return service.getSortedExpenses(sortBy, order);
    }

}