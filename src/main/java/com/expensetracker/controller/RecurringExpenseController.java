package com.expensetracker.controller;

import com.expensetracker.model.RecurringExpense;
import com.expensetracker.service.RecurringExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/recurring")
public class RecurringExpenseController {
    @Autowired
    private RecurringExpenseService service;

    @PostMapping
    public RecurringExpense addRecurring(@RequestBody RecurringExpense r) {
        return service.addRecurringExpense(r);
    }

    @GetMapping
    public List<RecurringExpense> getRecurringExpenses() {
        return service.getAllRecurring();
    }

    @DeleteMapping("/{id}")
    public void deleteRecurring(@PathVariable Long id) {
        service.deleteRecurringExpense(id);
    }
}
