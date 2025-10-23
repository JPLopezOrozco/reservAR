package com.reservAR.backreservar.repository;

import com.reservAR.backreservar.model.InventoryRule;
import com.reservAR.backreservar.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRuleRepository extends JpaRepository<InventoryRule, Long> {
    Optional<InventoryRule> findByRestaurant(Restaurant restaurant);
}
