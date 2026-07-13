package com.canarda.readiness.game.dto;

import com.canarda.readiness.game.domain.BuildingType;
import jakarta.validation.constraints.NotNull;

public record CreateBuildingRequest(@NotNull Long playerId, @NotNull BuildingType type) {
}
