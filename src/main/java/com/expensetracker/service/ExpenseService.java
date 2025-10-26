package com.expensetracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.ExpenseRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
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

    public List<Expense> getFilteredExpenses(String category, LocalDateTime start, LocalDateTime end, Double minAmount,
            Double maxAmount) {
        List<Expense> expenses = repo.findAll();

        if (category != null) {
            expenses = expenses.stream().filter(e -> e.getCategory().equalsIgnoreCase(category)).toList();
        }

        if (start != null && end != null) {
            expenses = expenses.stream().filter(e -> !e.getDate().isBefore(start) && !e.getDate().isAfter(end))
                    .toList();
        }

        if (minAmount != null) {
            expenses = expenses.stream().filter(e -> e.getAmount() >= minAmount).toList();
        }

        if (maxAmount != null) {
            expenses = expenses.stream().filter(e -> e.getAmount() <= maxAmount).toList();
        }
        return expenses;
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

    public Expense updateExpense(Long id, Expense expenseDetails) {
        Expense existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
        existing.setAmount(expenseDetails.getAmount());
        existing.setCategory(expenseDetails.getCategory());
        existing.setDate(expenseDetails.getDate());
        existing.setTitle(expenseDetails.getTitle());
        existing.setRecurrence(expenseDetails.getRecurrence());
        existing.setNote(expenseDetails.getNote());
        existing.setTags(expenseDetails.getTags());
        return repo.save(existing);
    }

    public List<Expense> getRecurringExpenses() {
        return repo.findByRecurrenceNot("none");
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void generateRecurringExpenses() {
        List<Expense> recurring = repo.findByRecurrenceNot("none");
        LocalDate today = LocalDate.now();

        for (Expense e : recurring) {
            if (!e.isActive())
                continue;
            if (e.getRecurrenceEndDate() != null && today.isAfter(e.getRecurrenceEndDate()))
                continue;
            LocalDate lastDate = e.getDate().toLocalDate();
            LocalDate nextDate = null;

            switch (e.getRecurrence().toLowerCase()) {
                case "daily":
                    nextDate = lastDate.plusDays(1);
                    break;
                case "weekly":
                    nextDate = lastDate.plusWeeks(1);
                    break;
                case "monthly":
                    nextDate = lastDate.plusMonths(1);
                    break;
                default:
                    continue;
            }

            if (nextDate.equals(today)) {
                Expense newExpense = new Expense();
                newExpense.setTitle(e.getTitle());
                newExpense.setAmount(e.getAmount());
                newExpense.setCategory(e.getCategory());
                newExpense.setDate(nextDate.atStartOfDay());
                newExpense.setRecurrence(e.getRecurrence());
                newExpense.setActive(true);

                e.setActive(false);
                repo.save(e);
                repo.save(newExpense);
            }
        }
    }

    public Expense setRecurringStatus(Long id, boolean status) {
        Expense exp = repo.findById(id).orElseThrow(() -> new RuntimeException("Expense not found"));
        exp.setActive(status);
        return repo.save(exp);
    }

    public List<Expense> searchExpenses(String keyword) {
        String keyy = keyword.toLowerCase();
        return repo.findAll().stream().filter(e -> (e.getTitle() != null && e.getTitle().toLowerCase().contains(keyy))
                || (e.getNote() != null && e.getNote().toLowerCase().contains(keyy))
                || (e.getTags() != null && e.getTags().toLowerCase().contains(keyy))).toList();
    }

    public List<Expense> getSortedExpenses(String sortBy, String order) {
        List<Expense> all = repo.findAll();
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
