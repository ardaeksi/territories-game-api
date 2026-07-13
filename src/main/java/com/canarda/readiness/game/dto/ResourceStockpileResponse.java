package com.canarda.readiness.game.dto;

import com.canarda.readiness.game.domain.ResourceType;

import java.util.Map;

public record ResourceStockpileResponse(Long playerId, Map<ResourceType, Double> amounts) {

    public static ResourceStockpileResponse of(Long playerId, Map<ResourceType, Double> amounts) {
        return new ResourceStockpileResponse(playerId, amounts);
    }
}
