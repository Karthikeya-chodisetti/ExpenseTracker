package com.expensetracker.controller;

import com.expensetracker.dto.RecurringExpenseRequestDTO;
import com.expensetracker.dto.RecurringExpenseResponseDTO;
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
    public RecurringExpenseResponseDTO addRecurring(
            @RequestBody RecurringExpenseRequestDTO dto) {

        return service.addRecurringExpense(dto);
    }

    @GetMapping
    public List<RecurringExpenseResponseDTO> getRecurringExpenses() {
        return service.getAllRecurring();
    }

    @DeleteMapping("/{id}")
    public void deleteRecurring(@PathVariable Long id) {
        service.deleteRecurringExpense(id);
    }
}