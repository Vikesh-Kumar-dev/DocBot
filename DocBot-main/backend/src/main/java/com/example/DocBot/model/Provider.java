package com.example.DocBot.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "providers")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Provider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String specialization;

    @Column(name = "clinic_name")
    private String clinicName;

    @Column(name = "is_registered", nullable = false)
    private Boolean isRegistered = false;

    @Column(name = "google_rating")
    private Double googleRating;

    @Column(name = "inapp_rating")
    private Double inAppRating;

    @Column(name = "consultation_price")
    private BigDecimal consultationPrice;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "contact_website")
    private String contactWebsite;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private String address;
}
