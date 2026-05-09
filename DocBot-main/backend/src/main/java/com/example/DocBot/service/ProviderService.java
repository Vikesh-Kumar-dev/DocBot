package com.example.DocBot.service;

import com.example.DocBot.dto.request.ProviderSearchRequest;
import com.example.DocBot.dto.response.ProviderResponse;
import com.example.DocBot.model.Provider;
import com.example.DocBot.model.ProviderAvailability;
import com.example.DocBot.repository.ProviderAvailabilityRepository;
import com.example.DocBot.repository.ProviderRepository;
import com.example.DocBot.util.LocationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProviderService {

    private static final Logger logger = LoggerFactory.getLogger(ProviderService.class);

    private final ProviderRepository providerRepository;
    private final ProviderAvailabilityRepository availabilityRepository;
    private final LocationUtils locationUtils;

    public ProviderService(ProviderRepository providerRepository,
                           ProviderAvailabilityRepository availabilityRepository,
                           LocationUtils locationUtils) {
        this.providerRepository = providerRepository;
        this.availabilityRepository = availabilityRepository;
        this.locationUtils = locationUtils;
    }

    /**
     * Search for providers by specialty within the given radius from user's location.
     * Splits results into registered and non-registered lists.
     * Registered providers include available time slots.
     */
    public ProviderResponse searchProviders(ProviderSearchRequest request) {
        List<Provider> providers = providerRepository.findBySpecialtyWithinRadius(
                request.getSpecialty(),
                request.getLatitude(),
                request.getLongitude(),
                request.getRadiusKm()
        );

        logger.info("Found {} providers for specialty '{}' within {}km",
                providers.size(), request.getSpecialty(), request.getRadiusKm());

        List<ProviderResponse.RegisteredProvider> registered = providers.stream()
                .filter(Provider::getIsRegistered)
                .map(p -> mapToRegisteredProvider(p, request.getLatitude(), request.getLongitude()))
                .collect(Collectors.toList());

        List<ProviderResponse.NonRegisteredProvider> nonRegistered = providers.stream()
                .filter(p -> !p.getIsRegistered())
                .map(p -> mapToNonRegisteredProvider(p, request.getLatitude(), request.getLongitude()))
                .collect(Collectors.toList());

        return ProviderResponse.builder()
                .registeredProviders(registered)
                .nonRegisteredProviders(nonRegistered)
                .build();
    }

    private ProviderResponse.RegisteredProvider mapToRegisteredProvider(Provider p, double userLat, double userLng) {
        double distance = locationUtils.calculateDistanceKm(userLat, userLng, p.getLatitude(), p.getLongitude());

        // Fetch available slots from today onwards
        List<ProviderAvailability> slots = availabilityRepository
                .findByProviderIdAndDateGreaterThanEqualAndIsBookedFalse(p.getId(), LocalDate.now());

        List<ProviderResponse.AvailableSlot> availableSlots = slots.stream()
                .map(slot -> ProviderResponse.AvailableSlot.builder()
                        .slotId(slot.getId())
                        .date(slot.getDate().toString())
                        .timeSlot(slot.getTimeSlot().toString())
                        .build())
                .collect(Collectors.toList());

        return ProviderResponse.RegisteredProvider.builder()
                .id(p.getId())
                .name(p.getName())
                .specialization(p.getSpecialization())
                .clinicName(p.getClinicName())
                .googleRating(p.getGoogleRating())
                .inAppRating(p.getInAppRating())
                .consultationPrice(p.getConsultationPrice())
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .address(p.getAddress())
                .distanceKm(Math.round(distance * 100.0) / 100.0)
                .availableSlots(availableSlots)
                .build();
    }

    private ProviderResponse.NonRegisteredProvider mapToNonRegisteredProvider(Provider p, double userLat, double userLng) {
        double distance = locationUtils.calculateDistanceKm(userLat, userLng, p.getLatitude(), p.getLongitude());

        return ProviderResponse.NonRegisteredProvider.builder()
                .id(p.getId())
                .name(p.getName())
                .specialization(p.getSpecialization())
                .googleRating(p.getGoogleRating())
                .contactPhone(p.getContactPhone())
                .contactWebsite(p.getContactWebsite())
                .latitude(p.getLatitude())
                .longitude(p.getLongitude())
                .address(p.getAddress())
                .distanceKm(Math.round(distance * 100.0) / 100.0)
                .build();
    }
}
