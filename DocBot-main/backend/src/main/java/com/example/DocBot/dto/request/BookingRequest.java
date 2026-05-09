package com.example.DocBot.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class BookingRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "Provider ID is required")
    private Long providerId;

    @NotNull(message = "Slot ID is required")
    private Long slotId;
}
