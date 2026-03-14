package com.expensetracker.repository;

import com.expensetracker.model.RecurringExpense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecurringExpenseRepository extends JpaRepository<RecurringExpense, Long> {
}
