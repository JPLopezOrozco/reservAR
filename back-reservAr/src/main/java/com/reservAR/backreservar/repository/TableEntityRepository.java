package com.reservAR.backreservar.repository;

import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.model.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TableEntityRepository extends JpaRepository<TableEntity, Long> {
    List<TableEntity> findByRestaurant(Restaurant restaurant);
    boolean existsByRestaurantAndCodeIgnoreCase(Restaurant restaurant, String code);
}
