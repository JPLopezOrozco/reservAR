package com.reservAR.backreservar.repository;

import com.reservAR.backreservar.model.Reservation;
import com.reservAR.backreservar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("""
            select r from Reservation r
            where r.status = 'BOOKED'
            and r.restaurant.id = :restaurantId
            and r.start >= :now_minus_window
            and r.start <= :now_minus_grace
""")
    List<Reservation> findByReservationBooked(@Param("restaurantId") Long restaurantId,
                                              @Param("now_minus_window")Instant now_minus_window,
                                              @Param("now_minus_grace") Instant now_minus_grace);

    List<Reservation> findAllByUser(User user);
}
