package com.example.DocBot.repository;

import com.example.DocBot.model.ConsentRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConsentRecordRepository extends JpaRepository<ConsentRecord, Long> {

    Optional<ConsentRecord> findByUserIdAndConsentType(Long userId, String consentType);

    boolean existsByUserIdAndConsentTypeAndAgreedTrue(Long userId, String consentType);
}
