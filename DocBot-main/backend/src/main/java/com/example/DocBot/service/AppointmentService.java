package com.example.DocBot.service;

import com.example.DocBot.dto.request.BookingRequest;
import com.example.DocBot.dto.response.AppointmentResponse;
import com.example.DocBot.exception.ResourceNotFoundException;
import com.example.DocBot.model.*;
import com.example.DocBot.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private static final Logger logger = LoggerFactory.getLogger(AppointmentService.class);

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;
    private final ProviderAvailabilityRepository availabilityRepository;
    private final N8nWebhookService n8nWebhookService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              UserRepository userRepository,
                              ProviderRepository providerRepository,
                              ProviderAvailabilityRepository availabilityRepository,
                              N8nWebhookService n8nWebhookService) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
        this.availabilityRepository = availabilityRepository;
        this.n8nWebhookService = n8nWebhookService;
    }

    @Transactional
    public AppointmentResponse bookAppointment(BookingRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", request.getUserId()));

        Provider provider = providerRepository.findById(request.getProviderId())
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "id", request.getProviderId()));

        if (!provider.getIsRegistered()) {
            throw new IllegalArgumentException("Cannot book with a non-registered provider");
        }

        ProviderAvailability slot = availabilityRepository.findById(request.getSlotId())
                .orElseThrow(() -> new ResourceNotFoundException("Slot", "id", request.getSlotId()));

        if (Boolean.TRUE.equals(slot.getIsBooked())) {
            throw new IllegalArgumentException("This time slot is already booked");
        }

        // Mark slot as booked
        slot.setIsBooked(true);
        availabilityRepository.save(slot);

        // Generate confirmation code
        String confirmationCode = "DB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Create appointment
        Appointment appointment = Appointment.builder()
                .user(user)
                .provider(provider)
                .slot(slot)
                .status("CONFIRMED")
                .confirmationCode(confirmationCode)
                .build();
        appointment = appointmentRepository.save(appointment);

        logger.info("Appointment booked: {} for user {} with provider {}",
                confirmationCode, user.getEmail(), provider.getName());

        // Trigger n8n webhook for email/SMS
        Map<String, Object> payload = new HashMap<>();
        payload.put("userName", user.getName());
        payload.put("userEmail", user.getEmail());
        payload.put("userPhone", user.getPhone());
        payload.put("providerName", provider.getName());
        payload.put("clinicName", provider.getClinicName());
        payload.put("appointmentDate", slot.getDate().toString());
        payload.put("appointmentTime", slot.getTimeSlot().toString());
        payload.put("address", provider.getAddress());
        payload.put("confirmationCode", confirmationCode);
        n8nWebhookService.triggerBookingWebhook(payload);

        return AppointmentResponse.builder()
                .appointmentId(appointment.getId())
                .confirmationCode(confirmationCode)
                .status("CONFIRMED")
                .message("Appointment confirmed! Check your email/SMS for details.")
                .providerName(provider.getName())
                .clinicName(provider.getClinicName())
                .appointmentDate(slot.getDate().toString())
                .appointmentTime(slot.getTimeSlot().toString())
                .address(provider.getAddress())
                .build();
    }

    public List<AppointmentResponse> getUserAppointments(Long userId) {
        return appointmentRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(apt -> AppointmentResponse.builder()
                        .appointmentId(apt.getId())
                        .confirmationCode(apt.getConfirmationCode())
                        .status(apt.getStatus())
                        .providerName(apt.getProvider().getName())
                        .clinicName(apt.getProvider().getClinicName())
                        .appointmentDate(apt.getSlot().getDate().toString())
                        .appointmentTime(apt.getSlot().getTimeSlot().toString())
                        .address(apt.getProvider().getAddress())
                        .build())
                .collect(Collectors.toList());
    }
}
