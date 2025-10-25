package com.expensetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

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

    public Map<String, Object> getSpendingSummary(String period, String category, String start, String end) {
        LocalDateTime from, to;

        if (period.equalsIgnoreCase("day")) {
            from = LocalDate.now().atStartOfDay();
            to = LocalDateTime.now();
        } else if (period.equalsIgnoreCase("week")) {
            from = LocalDate.now().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
            to = LocalDateTime.now();
        } else if (period.equalsIgnoreCase("month")) {
            from = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
            to = LocalDateTime.now();
        } else if (period.equalsIgnoreCase("custom") && start != null && end != null) {
            from = LocalDate.parse(start).atStartOfDay();
            to = LocalDate.parse(end).atTime(23, 59, 59);
        } else {
            throw new IllegalArgumentException("Invalid period or missing parameters");
        }

        List<Expense> expenses = repo.findByDateBetween(from, to);
        if (category != null) {
            expenses = expenses.stream().filter(e -> e.getCategory().equalsIgnoreCase(category)).toList();
        }

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        Map<String, Object> response = new HashMap<>();
        response.put("period", period);
        if (category != null)
            response.put("category", category);
        response.put("totalSpent", total);
        response.put("from", from.toLocalDate().toString());
        response.put("to", to.toLocalDate().toString());
        return response;
    }

    public Map<String, Object> getCategoryWiseSummary(String period, String start, String end) {
        LocalDateTime from, to;
        if (period.equalsIgnoreCase("day")) {
            from = LocalDate.now().atStartOfDay();
            to = LocalDateTime.now();
        } else if (period.equalsIgnoreCase("week")) {
            from = LocalDate.now().with(java.time.DayOfWeek.MONDAY).atStartOfDay();
            to = LocalDateTime.now();
        } else if (period.equalsIgnoreCase("month")) {
            from = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
            to = LocalDateTime.now();
        } else if (period.equalsIgnoreCase("custom") && start != null && end != null) {
            from = LocalDate.parse(start).atStartOfDay();
            to = LocalDate.parse(end).atTime(23, 59, 59);
        } else {
            throw new IllegalArgumentException("Invalid period or missing parameters");
        }

        List<Expense> expenses = repo.findByDateBetween(from, to);
        Map<String, Double> categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        Map<String, Object> response = new HashMap<>();
        response.put("from", from.toLocalDate().toString());
        response.put("to", to.toLocalDate().toString());
        response.put("categoryTotals", categoryTotals);
        return response;
    }

    public Map<String, Double> getDailySummary(String start, String end) {
        LocalDateTime from = LocalDate.parse(start).atStartOfDay();
        LocalDateTime to = LocalDate.parse(end).atTime(23, 59, 59);

        List<Expense> expenses = repo.findByDateBetween(from, to);
        return expenses.stream().collect(Collectors.groupingBy(e -> e.getDate().toLocalDate().toString(),
                Collectors.summingDouble(Expense::getAmount)));
    }
}
