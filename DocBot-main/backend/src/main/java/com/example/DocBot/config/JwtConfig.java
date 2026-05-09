package com.example.DocBot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;

@Configuration
@Getter
public class JwtConfig {

    @Value("${docbot.jwt.secret}")
    private String secret;

    @Value("${docbot.jwt.expiration-ms:86400000}")
    private long expirationMs; // default 24 hours
}
