package com.example.DocBot.repository;

import com.example.DocBot.model.SymptomAssessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SymptomAssessmentRepository extends JpaRepository<SymptomAssessment, Long> {

    Optional<SymptomAssessment> findBySessionId(String sessionId);
}
