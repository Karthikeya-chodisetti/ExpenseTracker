package com.expensetracker.dto;

import java.util.List;

public class InsightResponseDTO {

    private List<String> insights;

    public InsightResponseDTO() {
    }

    public InsightResponseDTO(List<String> insights) {
        this.insights = insights;
    }

    public List<String> getInsights() {
        return insights;
    }

    public void setInsights(List<String> insights) {
        this.insights = insights;
    }
}