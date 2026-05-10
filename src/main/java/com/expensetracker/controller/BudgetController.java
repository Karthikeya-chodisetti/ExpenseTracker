package com.expensetracker.controller;

import com.expensetracker.dto.BudgetRequestDTO;
import com.expensetracker.dto.BudgetResponseDTO;
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
    public BudgetResponseDTO setBudget(@RequestBody BudgetRequestDTO dto) {
        return service.setBudget(dto);
    }

    @GetMapping
    public List<BudgetResponseDTO> getBudgets() {
        return service.getBudgets();
    }

    @DeleteMapping("/{id}")
    public void deleteBudget(@PathVariable Long id) {
        service.deleteBudget(id);
    }
}