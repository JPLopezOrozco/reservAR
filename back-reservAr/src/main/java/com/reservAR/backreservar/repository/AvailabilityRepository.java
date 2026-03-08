package com.reservAR.backreservar.repository;

import com.reservAR.backreservar.model.Availability;
import com.reservAR.backreservar.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByRestaurant(Restaurant restaurant);
    @Query("""
        select (count(a) > 0) from Availability a
        where a.restaurant.id = :restaurantId
        and a.dayOfWeek = :dayOfWeek
        and a.start < :end
        and a.end > :start
""")
    boolean existsOverlap(@Param("restaurantId") Long restaurantId,
                          @Param("dayOfWeek") DayOfWeek dayOfWeek,
                          @Param("start") LocalTime start,
                          @Param("end") LocalTime end);


    @Query("""
 select a from Availability a
 where a.restaurant.id = :restId
   and a.dayOfWeek = :dow
   and a.start <= :nowLocal
   and a.end   >  :nowLocal
""")
    Optional<Availability> findOpenNow(@Param("restId") Long restId,
                                       @Param("dow") DayOfWeek dow,
                                       @Param("nowLocal") LocalTime nowLocal);

    @Query("""
        select (count(a) > 0) from Availability a
        where a.restaurant = :restaurant
        and a.dayOfWeek = :dayOfWeek
        and a.start <= :start
        and a.end >= :end
""")
    boolean isOpen(@Param("restaurant") Restaurant restaurant,
                   @Param("dayOfWeek") DayOfWeek dayOfWeek,
                   @Param("start") LocalTime start,
                   @Param("end") LocalTime end);
}
