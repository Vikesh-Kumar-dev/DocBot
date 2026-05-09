package com.example.DocBot.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "provider_availability")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProviderAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "provider_id", nullable = false)
    private Provider provider;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "time_slot", nullable = false)
    private LocalTime timeSlot;

    @Column(name = "is_booked", nullable = false)
    private Boolean isBooked = false;
}
