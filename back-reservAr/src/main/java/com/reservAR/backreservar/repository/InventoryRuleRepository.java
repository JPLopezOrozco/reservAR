package com.reservAR.backreservar.repository;

import com.reservAR.backreservar.model.InventoryRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRuleRepository extends JpaRepository<InventoryRule, Long> {
}
