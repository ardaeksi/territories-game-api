package com.canarda.readiness.game.dto;

import jakarta.validation.constraints.NotNull;

public record ClaimTerritoryRequest(@NotNull Long playerId) {
}
