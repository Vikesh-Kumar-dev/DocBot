package com.example.DocBot.service;

import com.example.DocBot.dto.request.ChatRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiAssessmentService {

    private static final Logger logger = LoggerFactory.getLogger(AiAssessmentService.class);

    private final WebClient webClient;

    public AiAssessmentService(@Value("${docbot.ai-service.url:http://ai-service:8000}") String aiServiceUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(aiServiceUrl)
                .build();
    }

    /**
     * Sends the user's message and full conversation history to the FastAPI AI service
     * at POST /assess. Returns the parsed response as a Map.
     *
     * Expected response fields from AI service:
     * - type: FOLLOW_UP | ASSESSMENT
     * - message: the AI response text
     * - recommendedSpecialty: e.g. "General Physician" (only when type=ASSESSMENT)
     * - isEmergency: boolean (secondary check on AI side)
     * - assessmentReady: boolean
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> assess(String message, List<ChatRequest.ChatMessage> history) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("message", message);
            requestBody.put("history", history);

            Map<String, Object> response = webClient.post()
                    .uri("/assess")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            logger.info("AI assessment response received: type={}", response != null ? response.get("type") : "null");
            return response;

        } catch (Exception ex) {
            logger.error("Failed to reach AI service: {}", ex.getMessage());

            // Fallback response when AI service is unreachable
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("type", "ERROR");
            fallback.put("message", "I'm sorry, I'm having trouble processing your request right now. " +
                    "Please try again in a moment. If you're experiencing a medical emergency, " +
                    "please call 112 or go to your nearest hospital.");
            fallback.put("isEmergency", false);
            fallback.put("assessmentReady", false);
            fallback.put("recommendedSpecialty", null);
            return fallback;
        }
    }
}
