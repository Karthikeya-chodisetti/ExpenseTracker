package com.expensetracker.service;

import com.expensetracker.dto.InsightResponseDTO;
import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.repository.ExpenseRepository;
import com.expensetracker.repository.UserRepository;
import com.expensetracker.service.GeminiService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private ExpenseRepository repo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private GeminiService geminiService;

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return userRepo.findByUsername(auth.getName())
                .orElseThrow();
    }

    public InsightResponseDTO generateInsights() {

        User user = getCurrentUser();

        List<Expense> expenses = repo.findByUser(user);

        List<String> insights = new ArrayList<>();

        if (expenses.isEmpty()) {
            insights.add("No expenses available for analysis");
            return new InsightResponseDTO(insights);
        }

        LocalDate now = LocalDate.now();

        LocalDateTime currentMonthStart = now.withDayOfMonth(1).atStartOfDay();

        LocalDateTime previousMonthStart = now.minusMonths(1).withDayOfMonth(1).atStartOfDay();

        LocalDateTime previousMonthEnd = now.withDayOfMonth(1).minusDays(1).atTime(23, 59, 59);

        List<Expense> currentMonthExpenses = expenses.stream()
                .filter(e -> e.getDate() != null &&
                        !e.getDate().isBefore(currentMonthStart))
                .toList();

        List<Expense> previousMonthExpenses = expenses.stream()
                .filter(e -> e.getDate() != null &&
                        !e.getDate().isBefore(previousMonthStart) && !e.getDate().isAfter(previousMonthEnd))
                .toList();

        double currentTotal = currentMonthExpenses.stream()
                .mapToDouble(Expense::getAmount).sum();

        double previousTotal = previousMonthExpenses.stream()
                .mapToDouble(Expense::getAmount).sum();

        if (previousTotal > 0) {

            double percent = ((currentTotal - previousTotal) / previousTotal) * 100;

            if (percent > 0) {
                insights.add(String.format("Your spending increased by %.2f%% compared to last month", percent));
            } else {
                insights.add(
                        String.format("Your spending decreased by %.2f%% compared to last month", Math.abs(percent)));
            }
        }

        Map<String, Double> categoryTotals = currentMonthExpenses.stream()
                .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)));

        categoryTotals.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .ifPresent(entry -> insights.add(entry.getKey() + " is your highest spending category this month"));

        double avg = currentTotal / Math.max(now.getDayOfMonth(), 1);

        insights.add(String.format("Average daily spending this month is %.2f", avg));

        return new InsightResponseDTO(insights);
    }

    public Map<String, String> generateAIInsights() {

        User user = getCurrentUser();

        List<Expense> expenses = repo.findByUser(user);

        if (expenses.isEmpty()) {
            return Map.of("insight", "No expenses available");
        }

        double total = expenses.stream().mapToDouble(Expense::getAmount).sum();

        Map<String, Double> categoryTotals = expenses.stream().collect(Collectors.groupingBy(
                Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        StringBuilder expenseData = new StringBuilder();

        expenseData.append("Total Spending: ").append(total).append("\n");

        categoryTotals.forEach((category, amount) -> {
            expenseData.append(category)
                    .append(": ")
                    .append(amount)
                    .append("\n");
        });

        String prompt = """
                Analyze the expense data.

                Return EXACTLY 4 lines in this format:

                Top Spending: <short sentence>
                Saving Tip: <short sentence>
                Bad Habit: <short sentence>
                Recommendation: <short sentence>

                Rules:
                - Keep each line under 20 words
                - Use complete sentences
                - No bullet points
                - No numbering
                - No extra explanation

                Expense Data:
                """ + expenseData;

        String aiResponse = geminiService.getAIInsights(prompt).trim();
        return Map.of("aiInsights", aiResponse);
    }
}
