package com.example.DocBot.service;

import com.example.DocBot.util.EmergencySymptomChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmergencyCheckService {

    private static final Logger logger = LoggerFactory.getLogger(EmergencyCheckService.class);

    private final EmergencySymptomChecker emergencySymptomChecker;

    public EmergencyCheckService(EmergencySymptomChecker emergencySymptomChecker) {
        this.emergencySymptomChecker = emergencySymptomChecker;
    }

    /**
     * Checks if the user's message contains any emergency symptom keywords.
     * This runs before every AI call and is rule-based (not AI-based).
     *
     * @param message the raw user message
     * @return true if emergency keywords are detected
     */
    public boolean isEmergency(String message) {
        boolean result = emergencySymptomChecker.isEmergency(message);
        if (result) {
            logger.warn("EMERGENCY DETECTED in message: {}", message);
        }
        return result;
    }

    /**
     * Returns the standard emergency response message.
     */
    public String getEmergencyMessage() {
        return emergencySymptomChecker.getEmergencyMessage();
    }
}
