package com.canarda.readiness.game.dto;

import com.canarda.readiness.game.domain.EquipmentType;
import jakarta.validation.constraints.NotNull;

public record StartCraftingJobRequest(
        @NotNull Long playerId,
        @NotNull EquipmentType equipmentType) {
}
