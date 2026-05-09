package com.example.DocBot.repository;

import com.example.DocBot.model.ProviderAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProviderAvailabilityRepository extends JpaRepository<ProviderAvailability, Long> {

    List<ProviderAvailability> findByProviderIdAndIsBookedFalse(Long providerId);

    List<ProviderAvailability> findByProviderIdAndDateAndIsBookedFalse(Long providerId, LocalDate date);

    List<ProviderAvailability> findByProviderIdAndDateGreaterThanEqualAndIsBookedFalse(Long providerId, LocalDate date);
}
