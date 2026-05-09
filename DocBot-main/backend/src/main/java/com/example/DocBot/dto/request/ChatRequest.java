package com.example.DocBot.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class ChatRequest {

    @NotBlank(message = "Session ID is required")
    private String sessionId;

    @NotBlank(message = "Message is required")
    private String message;

    private List<ChatMessage> history;

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class ChatMessage {
        private String role;
        private String content;
    }
}
