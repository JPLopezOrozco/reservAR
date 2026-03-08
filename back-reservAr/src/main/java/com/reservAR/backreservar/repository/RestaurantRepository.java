package com.reservAR.backreservar.repository;

import com.reservAR.backreservar.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    boolean existsByNameAndAddressAndCityIgnoreCaseAndIdNot(String name, String address, String city, Long id);

    @Query("""
        select r
        from Availability a
        join Restaurant r on r.id = a.restaurant.id
        where a.dayOfWeek = :dayOfWeek
        and a.start <= :now
        and a.end > :now
""")
    List<Restaurant> findAllRestaurantOpen(@Param("now") LocalTime now,@Param("dayOfWeek") DayOfWeek dayOfWeek);
}
