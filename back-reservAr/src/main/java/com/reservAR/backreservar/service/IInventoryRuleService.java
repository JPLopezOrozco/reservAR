package com.reservAR.backreservar.service;

import com.reservAR.backreservar.dto.InventoryRuleRequestDto;
import com.reservAR.backreservar.model.InventoryRule;

import java.util.List;

public interface IInventoryRuleService {
    InventoryRule findById(Long id);
    InventoryRule save(InventoryRuleRequestDto inventoryRule);
    List<InventoryRule> findAll();
    void deleteById(Long id);
}
