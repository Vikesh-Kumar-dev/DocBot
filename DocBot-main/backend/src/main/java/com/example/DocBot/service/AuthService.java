package com.example.DocBot.service;

import com.example.DocBot.dto.request.LoginRequest;
import com.example.DocBot.dto.request.RegisterRequest;
import com.example.DocBot.dto.response.AuthResponse;
import com.example.DocBot.exception.UnauthorizedException;
import com.example.DocBot.model.ConsentRecord;
import com.example.DocBot.model.User;
import com.example.DocBot.repository.ConsentRecordRepository;
import com.example.DocBot.repository.UserRepository;
import com.example.DocBot.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final ConsentRecordRepository consentRecordRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(UserRepository userRepository,
                       ConsentRecordRepository consentRecordRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.consentRecordRepository = consentRecordRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * Register a new user. Hashes the password, saves the user,
     * records consent if given, and returns a JWT token.
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .consentGiven(request.getConsentGiven() != null && request.getConsentGiven())
                .build();

        user = userRepository.save(user);
        logger.info("User registered successfully: {}", user.getEmail());

        // Record consent if given during registration
        if (Boolean.TRUE.equals(request.getConsentGiven())) {
            ConsentRecord consent = ConsentRecord.builder()
                    .user(user)
                    .consentType("HEALTH_DATA_STORAGE")
                    .agreed(true)
                    .build();
            consentRecordRepository.save(consent);
        }

        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getId());
        return new AuthResponse(token, user.getId());
    }

    /**
     * Authenticate a user with email and password.
     * Returns a JWT token on success.
     */
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        logger.info("User logged in successfully: {}", user.getEmail());
        String token = jwtTokenProvider.generateToken(user.getEmail(), user.getId());
        return new AuthResponse(token, user.getId());
    }
}
