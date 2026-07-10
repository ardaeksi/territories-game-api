package com.canarda.readiness.game.dto;

import com.canarda.readiness.game.domain.ResourceType;
import com.canarda.readiness.game.domain.Territory;

import java.util.Map;

public record TerritoryResponse(
        Long id,
        String countryId,
        String countryName,
        double centroidLat,
        double centroidLng,
        Long ownerId,
        String ownerDisplayName,
        String ownerColorHex,
        int population,
        Map<ResourceType, Integer> resources) {

    public static TerritoryResponse from(Territory territory) {
        return new TerritoryResponse(
                territory.getId(),
                territory.getCountryId(),
                territory.getCountryName(),
                territory.getCentroidLat(),
                territory.getCentroidLng(),
                territory.getOwner() != null ? territory.getOwner().getId() : null,
                territory.getOwner() != null ? territory.getOwner().getDisplayName() : null,
                territory.getOwner() != null ? territory.getOwner().getColorHex() : null,
                territory.getPopulation(),
                territory.getResources());
    }
}
