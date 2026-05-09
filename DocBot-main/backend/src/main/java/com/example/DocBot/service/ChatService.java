package com.example.DocBot.service;

import com.example.DocBot.dto.request.ChatRequest;
import com.example.DocBot.dto.response.ChatResponse;
import com.example.DocBot.model.ConversationLog;
import com.example.DocBot.model.SymptomAssessment;
import com.example.DocBot.model.User;
import com.example.DocBot.repository.ConversationLogRepository;
import com.example.DocBot.repository.SymptomAssessmentRepository;
import com.example.DocBot.repository.UserRepository;
import com.example.DocBot.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    private static final String DISCLAIMER = "⚕️ Disclaimer: DocBot is NOT a medical diagnosis tool. " +
            "The information provided is a preliminary assessment only and should not replace " +
            "professional medical advice, diagnosis, or treatment. Always consult a qualified " +
            "healthcare provider for medical concerns.";

    private final EmergencyCheckService emergencyCheckService;
    private final AiAssessmentService aiAssessmentService;
    private final ConversationLogRepository conversationLogRepository;
    private final SymptomAssessmentRepository symptomAssessmentRepository;
    private final UserRepository userRepository;

    public ChatService(EmergencyCheckService emergencyCheckService,
                       AiAssessmentService aiAssessmentService,
                       ConversationLogRepository conversationLogRepository,
                       SymptomAssessmentRepository symptomAssessmentRepository,
                       UserRepository userRepository) {
        this.emergencyCheckService = emergencyCheckService;
        this.aiAssessmentService = aiAssessmentService;
        this.conversationLogRepository = conversationLogRepository;
        this.symptomAssessmentRepository = symptomAssessmentRepository;
        this.userRepository = userRepository;
    }

    /**
     * Orchestrates the chat flow:
     * 1. Save user message to conversation log
     * 2. Run emergency check — if triggered, return emergency response immediately
     * 3. Fetch full conversation history from DB
     * 4. Send message + history to AI service
     * 5. Save assistant response to conversation log
     * 6. If assessment is ready, save to symptom_assessments
     * 7. Return response with disclaimer
     */
    @Transactional
    public ChatResponse processMessage(ChatRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        // Save user message
        saveConversationLog(user, request.getSessionId(), "USER", request.getMessage());

        // 1. Emergency check — runs before every AI call
        if (emergencyCheckService.isEmergency(request.getMessage())) {
            String emergencyMsg = emergencyCheckService.getEmergencyMessage();
            saveConversationLog(user, request.getSessionId(), "ASSISTANT", emergencyMsg);

            // Save emergency assessment
            SymptomAssessment assessment = SymptomAssessment.builder()
                    .sessionId(request.getSessionId())
                    .symptomsReported(request.getMessage())
                    .preliminaryAssessment("Emergency detected — user redirected to emergency services")
                    .isEmergency(true)
                    .build();
            symptomAssessmentRepository.save(assessment);

            return ChatResponse.builder()
                    .sessionId(request.getSessionId())
                    .role("ASSISTANT")
                    .message(emergencyMsg)
                    .type("EMERGENCY")
                    .isEmergency(true)
                    .assessmentReady(false)
                    .disclaimer(DISCLAIMER)
                    .build();
        }

        // 2. Fetch conversation history from DB for context
        List<ConversationLog> logs = conversationLogRepository
                .findBySessionIdOrderByTimestampAsc(request.getSessionId());

        List<ChatRequest.ChatMessage> history = logs.stream()
                .map(log -> new ChatRequest.ChatMessage(log.getRole().toLowerCase(), log.getMessage()))
                .collect(Collectors.toList());

        // 3. Call AI service
        Map<String, Object> aiResponse = aiAssessmentService.assess(request.getMessage(), history);

        String responseMessage = (String) aiResponse.get("message");
        String type = (String) aiResponse.get("type");
        Boolean isEmergency = (Boolean) aiResponse.getOrDefault("isEmergency", false);
        Boolean assessmentReady = (Boolean) aiResponse.getOrDefault("assessmentReady", false);
        String recommendedSpecialty = (String) aiResponse.get("recommendedSpecialty");

        // Secondary emergency check from AI side
        if (Boolean.TRUE.equals(isEmergency)) {
            responseMessage = emergencyCheckService.getEmergencyMessage();
            type = "EMERGENCY";
        }

        // Save assistant response
        saveConversationLog(user, request.getSessionId(), "ASSISTANT", responseMessage);

        // Save assessment if ready
        if (Boolean.TRUE.equals(assessmentReady)) {
            SymptomAssessment assessment = SymptomAssessment.builder()
                    .sessionId(request.getSessionId())
                    .symptomsReported(collectUserMessages(logs))
                    .preliminaryAssessment(responseMessage)
                    .recommendedSpecialty(recommendedSpecialty)
                    .isEmergency(false)
                    .build();
            symptomAssessmentRepository.save(assessment);
            logger.info("Assessment saved for session: {}", request.getSessionId());
        }

        return ChatResponse.builder()
                .sessionId(request.getSessionId())
                .role("ASSISTANT")
                .message(responseMessage)
                .type(type)
                .isEmergency(Boolean.TRUE.equals(isEmergency))
                .assessmentReady(Boolean.TRUE.equals(assessmentReady))
                .recommendedSpecialty(recommendedSpecialty)
                .disclaimer(DISCLAIMER)
                .build();
    }

    private void saveConversationLog(User user, String sessionId, String role, String message) {
        ConversationLog log = ConversationLog.builder()
                .user(user)
                .sessionId(sessionId)
                .role(role)
                .message(message)
                .build();
        conversationLogRepository.save(log);
    }

    private String collectUserMessages(List<ConversationLog> logs) {
        return logs.stream()
                .filter(log -> "USER".equals(log.getRole()))
                .map(ConversationLog::getMessage)
                .collect(Collectors.joining(" | "));
    }
}
