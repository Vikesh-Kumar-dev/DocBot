package com.example.DocBot.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long appointmentId;
    private String confirmationCode;
    private String status;
    private String message;
    private String providerName;
    private String clinicName;
    private String appointmentDate;
    private String appointmentTime;
    private String address;
}
