package com.example.DocBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class N8nWebhookService {

    private static final Logger logger = LoggerFactory.getLogger(N8nWebhookService.class);

    private final WebClient webClient;

    public N8nWebhookService(@Value("${docbot.n8n.webhook-url:http://localhost:5678/webhook/booking}") String webhookUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(webhookUrl)
                .build();
    }

    /**
     * Sends booking details to the n8n webhook to trigger the email/SMS notification workflow.
     * This is the only file that knows about n8n.
     *
     * @param bookingPayload map containing: userName, userEmail, userPhone, providerName,
     *                       clinicName, appointmentDate, appointmentTime, address, confirmationCode
     */
    public void triggerBookingWebhook(Map<String, Object> bookingPayload) {
        try {
            webClient.post()
                    .bodyValue(bookingPayload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            logger.info("n8n webhook triggered successfully for confirmation code: {}",
                    bookingPayload.get("confirmationCode"));
        } catch (Exception ex) {
            // Log but don't fail the booking — the appointment is already saved
            logger.error("Failed to trigger n8n webhook: {}. Booking is still saved.", ex.getMessage());
        }
    }
}
