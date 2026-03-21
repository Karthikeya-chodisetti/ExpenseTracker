package com.expensetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository repo;

    @Autowired
    private UserRepository userRepo;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepo.findByUsername(username).orElseThrow();
    }

    public List<Expense> getAllExpenses() {
        return repo.findByUser(getCurrentUser());
    }

    public Expense addExpense(Expense expense) {
        expense.setUser(getCurrentUser());
        return repo.save(expense);
    }

    public void deleteExpense(Long id) {
        User user = getCurrentUser();
        Expense exp = repo.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!exp.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        repo.deleteById(id);
    }

    public List<Expense> getExpensesByCategory(String category) {
        return repo.findByUser(getCurrentUser()).stream()
                .filter(e -> e.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    public List<Expense> getExpensesByDateRange(LocalDateTime start, LocalDateTime end) {
        return repo.findByUser(getCurrentUser()).stream()
                .filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
                .toList();
    }

    public List<Expense> getFilteredExpenses(String category, LocalDateTime start, LocalDateTime end,
            Double minAmount, Double maxAmount) {

        return repo.findByUser(getCurrentUser()).stream()
                .filter(e -> category == null || e.getCategory().equalsIgnoreCase(category))
                .filter(e -> start == null || !e.getDate().isBefore(start))
                .filter(e -> end == null || !e.getDate().isAfter(end))
                .filter(e -> minAmount == null || e.getAmount() >= minAmount)
                .filter(e -> maxAmount == null || e.getAmount() <= maxAmount)
                .toList();
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
            throw new IllegalArgumentException("Invalid period");
        }

        List<Expense> expenses = repo.findByUser(getCurrentUser()).stream()
                .filter(e -> !e.getDate().isBefore(from) && !e.getDate().isAfter(to))
                .toList();

        if (category != null) {
            expenses = expenses.stream()
                    .filter(e -> e.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        Map<String, Object> res = new HashMap<>();
        res.put("period", period);
        res.put("totalSpent", total);
        res.put("from", from.toLocalDate().toString());
        res.put("to", to.toLocalDate().toString());

        if (category != null)
            res.put("category", category);

        return res;
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
            throw new IllegalArgumentException("Invalid period");
        }

        List<Expense> expenses = repo.findByUser(getCurrentUser()).stream()
                .filter(e -> !e.getDate().isBefore(from) && !e.getDate().isAfter(to))
                .toList();

        Map<String, Double> map = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));

        Map<String, Object> res = new HashMap<>();
        res.put("from", from.toLocalDate().toString());
        res.put("to", to.toLocalDate().toString());
        res.put("categoryTotals", map);

        return res;
    }

    public Map<String, Double> getDailySummary(String start, String end) {

        LocalDateTime from = LocalDate.parse(start).atStartOfDay();
        LocalDateTime to = LocalDate.parse(end).atTime(23, 59, 59);

        List<Expense> expenses = repo.findByUser(getCurrentUser()).stream()
                .filter(e -> !e.getDate().isBefore(from) && !e.getDate().isAfter(to))
                .toList();

        return expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDate().toLocalDate().toString(),
                        Collectors.summingDouble(Expense::getAmount)));
    }

    public Expense updateExpense(Long id, Expense expenseDetails) {

        User user = getCurrentUser();
        Expense existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        existing.setAmount(expenseDetails.getAmount());
        existing.setCategory(expenseDetails.getCategory());
        existing.setDate(expenseDetails.getDate());
        existing.setTitle(expenseDetails.getTitle());
        existing.setNote(expenseDetails.getNote());
        existing.setTags(expenseDetails.getTags());

        return repo.save(existing);
    }

    public List<Expense> searchExpenses(String keyword) {

        String key = keyword.toLowerCase();

        return repo.findByUser(getCurrentUser()).stream()
                .filter(e -> (e.getTitle() != null && e.getTitle().toLowerCase().contains(key))
                        || (e.getNote() != null && e.getNote().toLowerCase().contains(key))
                        || (e.getTags() != null && e.getTags().toLowerCase().contains(key)))
                .toList();
    }

    public List<Expense> getSortedExpenses(String sortBy, String order) {

        List<Expense> all = repo.findByUser(getCurrentUser());

        Comparator<Expense> comp;

        switch (sortBy.toLowerCase()) {
            case "amount":
                comp = Comparator.comparingDouble(Expense::getAmount);
                break;
            case "title":
                comp = Comparator.comparing(Expense::getTitle, String.CASE_INSENSITIVE_ORDER);
                break;
            case "category":
                comp = Comparator.comparing(Expense::getCategory, String.CASE_INSENSITIVE_ORDER);
                break;
            default:
                comp = Comparator.comparing(Expense::getDate);
        }

        if (order.equalsIgnoreCase("desc"))
            comp = comp.reversed();

        return all.stream().sorted(comp).toList();
    }
}