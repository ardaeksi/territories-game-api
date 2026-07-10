package com.canarda.readiness.config;

import com.canarda.readiness.game.domain.ResourceType;
import com.canarda.readiness.game.domain.Territory;
import com.canarda.readiness.game.repository.TerritoryRepository;
import com.canarda.readiness.game.service.TerritoryAdjacencyService;
import com.canarda.readiness.game.service.TerritorySeedEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class GameDataSeeder implements CommandLineRunner {

    private static final int MIN_STARTING_RESOURCE = 50;
    private static final int RESOURCE_RANGE = 450;

    private final TerritoryRepository territoryRepository;
    private final TerritoryAdjacencyService territoryAdjacencyService;

    @Override
    public void run(String... args) {
        if (territoryRepository.count() > 0) {
            return;
        }

        for (TerritorySeedEntry entry : territoryAdjacencyService.getAllTerritories()) {
            territoryRepository.save(Territory.builder()
                    .countryId(entry.countryId())
                    .countryName(entry.countryName())
                    .centroidLat(entry.centroidLat())
                    .centroidLng(entry.centroidLng())
                    .population(0)
                    .resources(startingResourcesFor(entry.countryId()))
                    .build());
        }
    }

    // Deterministic per-territory: same country always gets the same starting resources
    // across a fresh reseed, rather than shuffling every time the table is repopulated.
    private Map<ResourceType, Integer> startingResourcesFor(String countryId) {
        Random random = new Random(countryId.hashCode());
        Map<ResourceType, Integer> resources = new HashMap<>();
        for (ResourceType type : ResourceType.values()) {
            resources.put(type, MIN_STARTING_RESOURCE + random.nextInt(RESOURCE_RANGE));
        }
        return resources;
    }
}
