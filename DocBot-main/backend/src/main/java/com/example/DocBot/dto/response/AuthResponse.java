package com.example.DocBot.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long userId;

    public AuthResponse(String accessToken, Long userId) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.userId = userId;
    }
}
