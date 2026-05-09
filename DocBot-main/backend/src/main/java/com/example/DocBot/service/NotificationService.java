package com.example.DocBot.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    public void sendEmail(String to, String subject, String body) {
        logger.info("Email notification stub — to: {}, subject: {}", to, subject);
    }

    public void sendSms(String phoneNumber, String message) {
        logger.info("SMS notification stub — to: {}", phoneNumber);
    }
}
