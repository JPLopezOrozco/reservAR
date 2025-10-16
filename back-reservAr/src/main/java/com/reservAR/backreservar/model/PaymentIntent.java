package com.reservAR.backreservar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "payments")
public class PaymentIntent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Reservation reservation;
    private String provider;
    @Column(precision=12, scale=2, nullable=false)
    private BigDecimal amount;
    private String currency;
    @Enumerated(EnumType.STRING)
    @Column(length=3, nullable=false)
    private PaymentStatus status;
    private String externalId;
}
