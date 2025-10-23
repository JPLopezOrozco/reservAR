package com.reservAR.backreservar.repository;

import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.model.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface TableEntityRepository extends JpaRepository<TableEntity, Long> {
    List<TableEntity> findByRestaurant(Restaurant restaurant);
    boolean existsByRestaurantAndCodeIgnoreCase(Restaurant restaurant, String code);
    @Query("""
            select t from TableEntity t
             where t.restaurant.id = :restaurantId
             and not exists(
                select 1
                from Reservation r
                join r.tables rt
                where rt.id = t.id
                    and r.status = 'BOOKED'
                    and r.start < :end
                    and r.end > :start
             )
             order by t.maxCapacity desc
""")
    List<TableEntity> findFreeTablesByRestaurant(@Param("restaurantId") Long restaurantId,
                                                 @Param("start") Instant start,
                                                 @Param("end") Instant end);
 }
