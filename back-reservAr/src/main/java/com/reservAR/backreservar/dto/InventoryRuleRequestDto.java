package com.reservAR.backreservar.dto;

import jakarta.validation.constraints.NotNull;

public record InventoryRuleRequestDto(
        @NotNull Long restaurantId,
        @NotNull int defaultDuration,
        @NotNull int prepBufferMin,
        @NotNull int cleanupBufferMin,
        @NotNull int slotGranularityMin
) {
}
