package com.expensetracker.service;

import com.expensetracker.dto.BudgetRequestDTO;
import com.expensetracker.dto.BudgetResponseDTO;

import com.expensetracker.model.Budget;
import com.expensetracker.repository.BudgetRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository repo;

    private BudgetResponseDTO mapToDTO(Budget budget) {

        return new BudgetResponseDTO(
                budget.getId(),
                budget.getCategory(),
                budget.getLimitAmount());
    }

    public BudgetResponseDTO setBudget(BudgetRequestDTO dto) {

        Budget budget = new Budget();

        budget.setCategory(dto.getCategory());
        budget.setLimitAmount(dto.getLimitAmount());

        Budget saved = repo.save(budget);

        return mapToDTO(saved);
    }

    public List<BudgetResponseDTO> getBudgets() {

        return repo.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public BudgetResponseDTO getBudgetByCategory(String category) {

        Budget budget = repo.findByCategory(category)
                .stream()
                .findFirst()
                .orElse(null);

        if (budget == null) {
            return null;
        }

        return mapToDTO(budget);
    }

    public void deleteBudget(Long id) {
        repo.deleteById(id);
    }
}