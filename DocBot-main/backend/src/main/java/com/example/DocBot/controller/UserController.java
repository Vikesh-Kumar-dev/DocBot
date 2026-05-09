package com.example.DocBot.controller;

import com.example.DocBot.dto.response.AppointmentResponse;
import com.example.DocBot.exception.ResourceNotFoundException;
import com.example.DocBot.model.User;
import com.example.DocBot.repository.UserRepository;
import com.example.DocBot.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserRepository userRepository;
    private final AppointmentService appointmentService;

    public UserController(UserRepository userRepository,
                          AppointmentService appointmentService) {
        this.userRepository = userRepository;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile() {
        User user = getCurrentUser();
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", user.getId());
        profile.put("name", user.getName());
        profile.put("email", user.getEmail());
        profile.put("phone", user.getPhone());
        profile.put("consentGiven", user.getConsentGiven());
        profile.put("createdAt", user.getCreatedAt().toString());
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> getUserAppointments() {
        User user = getCurrentUser();
        List<AppointmentResponse> appointments = appointmentService.getUserAppointments(user.getId());
        return ResponseEntity.ok(appointments);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }
}
