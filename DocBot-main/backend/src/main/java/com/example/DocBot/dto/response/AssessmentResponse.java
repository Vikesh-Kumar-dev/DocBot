package com.example.DocBot.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AssessmentResponse {
    private String preliminaryAssessment;
    private String recommendedSpecialty;
    private String disclaimer;
}
