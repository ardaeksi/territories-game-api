package com.canarda.readiness.game.dto;

import com.canarda.readiness.game.domain.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record StartMiningJobRequest(
        @NotNull Long playerId,
        @NotNull ResourceType resourceType,
        @NotBlank String presetKey) {
}
