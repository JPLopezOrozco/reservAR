package com.reservAR.backreservar.model;

import com.reservAR.backreservar.exception.AvailabilityException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "restaurant_availability", uniqueConstraints = @UniqueConstraint(columnNames = {"resturant_id" ,"day_of_week"}))
public class Availability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Restaurant restaurant;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;
    @Column(nullable = false, name = "start_time")
    private LocalTime start;
    @Column(nullable = false, name = "end_time")
    private LocalTime end;


    @PrePersist
    @PreUpdate
    private void validate() {
        if (!start.isBefore(end)) {
            throw new AvailabilityException("end cannot be before start");
        }
    }
}
