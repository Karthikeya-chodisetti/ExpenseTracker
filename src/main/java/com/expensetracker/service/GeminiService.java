package com.expensetracker.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://openrouter.ai/api/v1")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .build();

    public String getAIInsights(String prompt) {

        Map<String, Object> body = Map.of(
                "model", "openai/gpt-3.5-turbo",
                "messages", List.of(
                        Map.of("role", "user", "content", prompt)));

        try {

            Map<String, Object> response = webClient.post()
                    .uri("/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            return extractText(response);

        } catch (Exception e) {

            e.printStackTrace();

            String msg = e.getMessage();

            if (msg != null && msg.contains("429")) {
                return "AI quota exceeded temporarily. Try again later.";
            }

            if (msg != null && msg.contains("401")) {
                return "Invalid OpenRouter API key.";
            }

            return "Failed to generate AI insights.";
        }
    }

    @SuppressWarnings("unchecked")
    private String extractText(Map<String, Object> response) {

        try {

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");

            if (choices == null || choices.isEmpty()) {
                return "No AI response returned.";
            }

            Map<String, Object> firstChoice = choices.get(0);

            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");

            if (message == null) {
                return "No message returned.";
            }

            Object content = message.get("content");

            return content != null
                    ? content.toString()
                    : "Empty AI response.";

        } catch (Exception e) {

            e.printStackTrace();
            return "Failed to parse AI response.";
        }
    }
}