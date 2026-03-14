package com.expensetracker.service;

import com.expensetracker.model.Budget;
import com.expensetracker.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BudgetService {
    @Autowired
    private BudgetRepository repo;

    public Budget setBudget(Budget budget) {
        return repo.save(budget);
    }

    public List<Budget> getBudgets() {
        return repo.findAll();
    }

    public Budget getBudgetByCategory(String category) {
        return repo.findByCategory(category).stream().findFirst().orElse(null);
    }

    public void deleteBudget(Long id) {
        repo.deleteById(id);
    }
}
