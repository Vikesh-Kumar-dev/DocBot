package com.example.DocBot.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "symptom_assessments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class SymptomAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "symptoms_reported", nullable = false, columnDefinition = "TEXT")
    private String symptomsReported;

    @Column(name = "preliminary_assessment", columnDefinition = "TEXT")
    private String preliminaryAssessment;

    @Column(name = "recommended_specialty")
    private String recommendedSpecialty;

    @Column(name = "is_emergency", nullable = false)
    private Boolean isEmergency = false;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = LocalDateTime.now();
    }
}
