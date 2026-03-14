package com.expensetracker.controller;

import com.expensetracker.model.Budget;
import com.expensetracker.service.BudgetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/budgets")
public class BudgetController {
    @Autowired
    private BudgetService service;

    @PostMapping
    public Budget setBudget(@RequestBody Budget budget) {
        return service.setBudget(budget);
    }

    @GetMapping
    public List<Budget> getBudgets() {
        return service.getBudgets();
    }

    @DeleteMapping("/{id}")
    public void deleteBudget(@PathVariable Long id) {
        service.deleteBudget(id);
    }
}
