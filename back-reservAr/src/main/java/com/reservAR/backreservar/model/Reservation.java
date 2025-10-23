package com.reservAR.backreservar.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

@Entity
@Setter
@Getter
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
    @Column(name = "start_time")
    private Instant start;
    @Column(name = "end_time")
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
