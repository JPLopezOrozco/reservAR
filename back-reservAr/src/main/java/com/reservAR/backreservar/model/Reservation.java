package com.reservAR.backreservar.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reservations")
public class Reservation {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private Instant start;
    private Instant end;
    @Enumerated(EnumType.STRING)
    private Status status;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name="reservation_tables",
            joinColumns=@JoinColumn(name="reservation_id"),
            inverseJoinColumns=@JoinColumn(name="table_id"))
    private List<TableEntity> tables;
    @Version
    private Long version;
}
