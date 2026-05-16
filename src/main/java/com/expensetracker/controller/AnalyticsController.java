package com.expensetracker.controller;

import com.expensetracker.dto.InsightResponseDTO;
import com.expensetracker.service.AnalyticsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService service;

    @GetMapping("/insights")
    public InsightResponseDTO getInsights() {
        return service.generateInsights();
    }

    @GetMapping("/ai-insights")
    public Map<String, String> getAIInsights() {
        return service.generateAIInsights();
    }
}