package com.expensetracker.service;

import com.expensetracker.model.RecurringExpense;
import com.expensetracker.model.Expense;
import com.expensetracker.repository.RecurringExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecurringExpenseService {

    @Autowired
    private RecurringExpenseRepository repo;

    @Autowired
    private ExpenseService expenseService;

    public RecurringExpense addRecurringExpense(RecurringExpense r) {
        return repo.save(r);
    }

    public List<RecurringExpense> getAllRecurring() {
        return repo.findAll();
    }

    public void deleteRecurringExpense(Long id) {
        repo.deleteById(id);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void processRecurringExpenses() {

        LocalDateTime now = LocalDateTime.now();
        List<RecurringExpense> recurringList = repo.findAll();

        for (RecurringExpense r : recurringList) {

            if (r.getNextDate() == null || r.getFrequency() == null) {
                continue;
            }

            int safetyCounter = 0;

            while (!r.getNextDate().isAfter(now)) {

                Expense expense = new Expense();
                expense.setTitle(r.getTitle());
                expense.setAmount(r.getAmount());
                expense.setCategory(r.getCategory());
                expense.setNote(r.getNote());
                expense.setTags(r.getTags());
                expense.setDate(r.getNextDate());
                expense.setActive(true);

                expenseService.addExpense(expense);

                switch (r.getFrequency().toLowerCase()) {
                    case "daily" -> r.setNextDate(r.getNextDate().plusDays(1));
                    case "weekly" -> r.setNextDate(r.getNextDate().plusWeeks(1));
                    case "monthly" -> r.setNextDate(r.getNextDate().plusMonths(1));
                    default -> {
                        return;
                    }
                }

                safetyCounter++;
                if (safetyCounter > 1000) {
                    break;
                }
            }

            repo.save(r);
        }
    }
}