package com.expensetracker.service;

import com.expensetracker.dto.ExpenseRequestDTO;
import com.expensetracker.dto.RecurringExpenseRequestDTO;
import com.expensetracker.dto.RecurringExpenseResponseDTO;

import com.expensetracker.model.RecurringExpense;
import com.expensetracker.repository.RecurringExpenseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RecurringExpenseService {

    @Autowired
    private RecurringExpenseRepository repo;

    @Autowired
    private ExpenseService expenseService;

    public RecurringExpenseResponseDTO addRecurringExpense(
            RecurringExpenseRequestDTO dto) {

        RecurringExpense r = new RecurringExpense();

        r.setTitle(dto.getTitle());
        r.setAmount(dto.getAmount());
        r.setCategory(dto.getCategory());
        r.setNote(dto.getNote());
        r.setTags(dto.getTags());
        r.setFrequency(dto.getFrequency());
        r.setNextDate(dto.getNextDate());

        return mapToDTO(repo.save(r));
    }

    public List<RecurringExpenseResponseDTO> getAllRecurring() {

        return repo.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
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

                ExpenseRequestDTO dto = new ExpenseRequestDTO();

                dto.setTitle(r.getTitle());
                dto.setAmount(r.getAmount());
                dto.setCategory(r.getCategory());
                dto.setNote(r.getNote());
                dto.setTags(r.getTags());
                dto.setDate(r.getNextDate());

                expenseService.addExpense(dto);

                switch (r.getFrequency().toLowerCase()) {

                    case "daily" ->
                        r.setNextDate(r.getNextDate().plusDays(1));

                    case "weekly" ->
                        r.setNextDate(r.getNextDate().plusWeeks(1));

                    case "monthly" ->
                        r.setNextDate(r.getNextDate().plusMonths(1));

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

    private RecurringExpenseResponseDTO mapToDTO(
            RecurringExpense r) {

        return new RecurringExpenseResponseDTO(
                r.getId(),
                r.getTitle(),
                r.getAmount(),
                r.getCategory(),
                r.getNote(),
                r.getTags(),
                r.getFrequency(),
                r.getNextDate());
    }
}