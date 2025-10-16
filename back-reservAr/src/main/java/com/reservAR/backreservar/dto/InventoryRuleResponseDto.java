package com.reservAR.backreservar.dto;

import com.reservAR.backreservar.model.InventoryRule;

public record InventoryRuleResponseDto(
        Long restaurantId,
        String restaurantName,
        int defaultDurationMin,
        int prepBufferMin,
        int cleanupBufferMin,
        int slotGranularityMin
) {
    public static InventoryRuleResponseDto of(InventoryRule inventoryRule) {
        return new InventoryRuleResponseDto(
                inventoryRule.getRestaurant().getId(),
                inventoryRule.getRestaurant().getName(),
                inventoryRule.getDefaultDurationMin(),
                inventoryRule.getPrepBufferMin(),
                inventoryRule.getCleanupBufferMin(),
                inventoryRule.getSlotGranularityMin()
        );
    }
}
