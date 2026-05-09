package com.example.DocBot.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ChatResponse {
    private String sessionId;
    private String role;
    private String message;
    private String type;               // FOLLOW_UP, ASSESSMENT, or EMERGENCY
    private Boolean isEmergency;
    private Boolean assessmentReady;
    private String recommendedSpecialty;
    private String disclaimer;
}
