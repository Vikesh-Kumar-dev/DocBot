package com.example.DocBot.util;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmergencySymptomChecker {

    private static final List<String> EMERGENCY_KEYWORDS = List.of(
            "chest pain",
            "difficulty breathing",
            "can't breathe",
            "cannot breathe",
            "shortness of breath",
            "severe bleeding",
            "heavy bleeding",
            "unconscious",
            "loss of consciousness",
            "fainted",
            "fainting",
            "seizure",
            "convulsion",
            "stroke",
            "sudden numbness",
            "sudden weakness",
            "paralysis",
            "suicidal",
            "suicide",
            "self harm",
            "self-harm",
            "overdose",
            "poisoning",
            "severe allergic reaction",
            "anaphylaxis",
            "choking",
            "severe head injury",
            "head trauma",
            "severe burn",
            "heart attack",
            "cardiac arrest",
            "coughing blood",
            "vomiting blood",
            "blood in stool",
            "sudden severe headache",
            "sudden vision loss",
            "high fever with rash",
            "difficulty swallowing",
            "severe abdominal pain"
    );

    /**
     * Checks if the user's message contains any emergency symptom keywords.
     * This is a deterministic, rule-based check — not AI-based.
     *
     * @param message the user's raw message text
     * @return true if an emergency keyword is detected
     */
    public boolean isEmergency(String message) {
        if (message == null || message.isBlank()) {
            return false;
        }
        String lowerMessage = message.toLowerCase().trim();
        return EMERGENCY_KEYWORDS.stream().anyMatch(lowerMessage::contains);
    }

    /**
     * Returns the emergency response message shown to users when red flags are detected.
     */
    public String getEmergencyMessage() {
        return "🚨 EMERGENCY ALERT: Based on the symptoms you've described, " +
               "this could be a medical emergency.\n\n" +
               "Please take immediate action:\n" +
               "• Call Emergency Services: 112 (India)\n" +
               "• Call an Ambulance: 108\n" +
               "• Go to the nearest hospital emergency room immediately\n\n" +
               "Do NOT wait for an online consultation. Your safety is the top priority.\n\n" +
               "If you are with someone experiencing these symptoms, " +
               "please help them get emergency medical attention right away.";
    }
}
