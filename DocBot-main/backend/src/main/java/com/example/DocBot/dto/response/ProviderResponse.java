package com.example.DocBot.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProviderResponse {

    private List<RegisteredProvider> registeredProviders;
    private List<NonRegisteredProvider> nonRegisteredProviders;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class RegisteredProvider {
        private Long id;
        private String name;
        private String specialization;
        private String clinicName;
        private Double googleRating;
        private Double inAppRating;
        private BigDecimal consultationPrice;
        private Double latitude;
        private Double longitude;
        private String address;
        private Double distanceKm;
        private List<AvailableSlot> availableSlots;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class NonRegisteredProvider {
        private Long id;
        private String name;
        private String specialization;
        private Double googleRating;
        private String contactPhone;
        private String contactWebsite;
        private Double latitude;
        private Double longitude;
        private String address;
        private Double distanceKm;
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    @Builder
    public static class AvailableSlot {
        private Long slotId;
        private String date;
        private String timeSlot;
    }
}
