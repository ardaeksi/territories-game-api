package com.canarda.readiness.game.service;

import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.game.domain.Building;
import com.canarda.readiness.game.domain.BuildingType;
import com.canarda.readiness.game.domain.Territory;
import com.canarda.readiness.game.repository.BuildingRepository;
import com.canarda.readiness.game.repository.TerritoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final TerritoryRepository territoryRepository;
    private final GameResourceService gameResourceService;

    public List<Building> findByTerritory(Long territoryId) {
        return buildingRepository.findByTerritoryId(territoryId);
    }

    public Building construct(Long territoryId, Long playerId, BuildingType type) {
        Territory territory = territoryRepository.findById(territoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Territory not found: " + territoryId));

        if (territory.getOwner() == null || !territory.getOwner().getId().equals(playerId)) {
            throw new IllegalStateException("You don't own this territory");
        }

        if (buildingRepository.existsByTerritoryIdAndType(territoryId, type)) {
            throw new IllegalStateException(
                    "A " + type + " already exists on " + territory.getCountryName());
        }

        BuildingBlueprint blueprint = BuildingBlueprint.of(type);
        gameResourceService.spend(playerId, blueprint.cost());

        Building building = Building.builder()
                .territory(territory)
                .type(type)
                .builtAt(LocalDateTime.now())
                .build();
        return buildingRepository.save(building);
    }
}
