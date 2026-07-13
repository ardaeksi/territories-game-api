package com.canarda.readiness.game.dto;

import com.canarda.readiness.game.domain.Building;
import com.canarda.readiness.game.domain.BuildingType;

import java.time.LocalDateTime;

public record BuildingResponse(
        Long id,
        Long territoryId,
        String territoryName,
        BuildingType type,
        LocalDateTime builtAt) {

    public static BuildingResponse from(Building building) {
        return new BuildingResponse(
                building.getId(),
                building.getTerritory().getId(),
                building.getTerritory().getCountryName(),
                building.getType(),
                building.getBuiltAt());
    }
}
