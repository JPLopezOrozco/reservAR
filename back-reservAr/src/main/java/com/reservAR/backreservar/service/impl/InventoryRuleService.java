package com.reservAR.backreservar.service.impl;

import com.reservAR.backreservar.dto.InventoryRuleRequestDto;
import com.reservAR.backreservar.exception.InventoryRuleException;
import com.reservAR.backreservar.exception.InventoryRuleNotFoundException;
import com.reservAR.backreservar.model.InventoryRule;
import com.reservAR.backreservar.model.Restaurant;
import com.reservAR.backreservar.repository.InventoryRuleRepository;
import com.reservAR.backreservar.service.IInventoryRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryRuleService implements IInventoryRuleService {

    private final InventoryRuleRepository inventoryRuleRepository;
    private final RestaurantService restaurantService;

    @Override
    public InventoryRule findById(Long id) {
        return inventoryRuleRepository.findById(id)
                .orElseThrow(()-> new InventoryRuleNotFoundException("Rule not found"));
    }

    @Override
    public InventoryRule save(InventoryRuleRequestDto inventoryRule) {
        if (inventoryRule.cleanupBufferMin() < 0
                || inventoryRule.defaultDuration() < 0
                || inventoryRule.prepBufferMin() < 0
                || inventoryRule.slotGranularityMin() < 0
        )
            throw new InventoryRuleException("Rule invalid");
        Restaurant restaurant = restaurantService.findById(inventoryRule.restaurantId());
        InventoryRule newInventoryRule = InventoryRule.builder()
                .restaurant(restaurant)
                .defaultDurationMin(inventoryRule.defaultDuration())
                .prepBufferMin(inventoryRule.prepBufferMin())
                .cleanupBufferMin(inventoryRule.cleanupBufferMin())
                .slotGranularityMin(inventoryRule.slotGranularityMin())
                .build();

        return inventoryRuleRepository.save(newInventoryRule);
    }

    @Override
    public List<InventoryRule> findAll() {
        return inventoryRuleRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        InventoryRule inventoryRule = inventoryRuleRepository.findById(id)
                .orElseThrow(()-> new InventoryRuleNotFoundException("Rule not found"));
        inventoryRuleRepository.delete(inventoryRule);
    }
}
