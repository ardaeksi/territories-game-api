package com.canarda.readiness.game.dto;

import com.canarda.readiness.game.domain.EquipmentType;

import java.util.Map;

public record GameEquipmentResponse(Long playerId, Map<EquipmentType, Integer> amounts) {

    public static GameEquipmentResponse of(Long playerId, Map<EquipmentType, Integer> amounts) {
        return new GameEquipmentResponse(playerId, amounts);
    }
}
