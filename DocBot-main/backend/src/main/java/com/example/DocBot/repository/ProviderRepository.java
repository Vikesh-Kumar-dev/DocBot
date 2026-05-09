package com.example.DocBot.repository;

import com.example.DocBot.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    /**
     * Find providers by specialty within a given radius (in km) from user's coordinates.
     * Uses the Haversine formula directly in SQL for distance calculation.
     */
    @Query(value = "SELECT * FROM providers p " +
            "WHERE LOWER(p.specialization) = LOWER(:specialty) " +
            "AND ( " +
            "    6371 * acos( " +
            "        cos(radians(:lat)) * cos(radians(p.latitude)) " +
            "        * cos(radians(p.longitude) - radians(:lng)) " +
            "        + sin(radians(:lat)) * sin(radians(p.latitude)) " +
            "    ) " +
            ") <= :radiusKm " +
            "ORDER BY p.is_registered DESC, " +
            "         (6371 * acos( " +
            "            cos(radians(:lat)) * cos(radians(p.latitude)) " +
            "            * cos(radians(p.longitude) - radians(:lng)) " +
            "            + sin(radians(:lat)) * sin(radians(p.latitude)) " +
            "         )) ASC", nativeQuery = true)
    List<Provider> findBySpecialtyWithinRadius(
            @Param("specialty") String specialty,
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radiusKm") double radiusKm
    );

    List<Provider> findBySpecializationIgnoreCase(String specialization);
}
