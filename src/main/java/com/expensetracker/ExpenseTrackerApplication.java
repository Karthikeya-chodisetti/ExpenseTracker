package com.expensetracker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.expensetracker.repository.BudgetRepository;
import com.expensetracker.repository.RecurringExpenseRepository;

@SpringBootApplication
@EnableScheduling

public class ExpenseTrackerApplication {

	@Autowired
	private BudgetRepository budgetRepository;

	@Autowired
	private RecurringExpenseRepository recurringExpenseRepository;

	public static void main(String[] args) {
		SpringApplication.run(ExpenseTrackerApplication.class, args);
	}

	@Configuration
	static class SecurityConfig {
		@Bean
		public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
			http
					.csrf(csrf -> csrf.disable())
					.authorizeHttpRequests(auth -> auth
							.anyRequest().permitAll());
			return http.build();
		}
	}
}
