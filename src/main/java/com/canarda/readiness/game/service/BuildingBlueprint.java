package com.canarda.readiness.game.service;

import com.canarda.readiness.game.domain.BuildingType;
import com.canarda.readiness.game.domain.ResourceType;

import java.util.Map;

/**
 * A building's construction cost and what it unlocks. Static game-design data - not
 * player-editable, so it lives as code rather than a seeded table, same rationale as
 * why territory adjacency is a checked-in resource file rather than computed at runtime.
 */
public record BuildingBlueprint(
        BuildingType type,
        Map<ResourceType, Integer> cost,
        ResourceType unlocksSpendingOf) {

    private static final Map<BuildingType, BuildingBlueprint> BLUEPRINTS = Map.of(
            BuildingType.MINE, new BuildingBlueprint(
                    BuildingType.MINE,
                    Map.of(ResourceType.WOOD, 200, ResourceType.STONE, 300),
                    ResourceType.METAL),
            BuildingType.OIL_RIG, new BuildingBlueprint(
                    BuildingType.OIL_RIG,
                    Map.of(ResourceType.WOOD, 250, ResourceType.STONE, 200, ResourceType.METAL, 100),
                    ResourceType.OIL),
            BuildingType.GUNPOWDER_FACTORY, new BuildingBlueprint(
                    BuildingType.GUNPOWDER_FACTORY,
                    Map.of(ResourceType.WOOD, 300, ResourceType.STONE, 300, ResourceType.METAL, 150),
                    ResourceType.GUNPOWDER),
            BuildingType.BOAT_FACTORY, new BuildingBlueprint(
                    BuildingType.BOAT_FACTORY,
                    Map.of(ResourceType.WOOD, 400, ResourceType.STONE, 400, ResourceType.METAL, 250),
                    null),
            BuildingType.ARMY_CAMP, new BuildingBlueprint(
                    BuildingType.ARMY_CAMP,
                    Map.of(ResourceType.WOOD, 200, ResourceType.STONE, 150, ResourceType.FOOD, 100),
                    null),
            BuildingType.ARMY_FACTORY, new BuildingBlueprint(
                    BuildingType.ARMY_FACTORY,
                    Map.of(ResourceType.WOOD, 350, ResourceType.STONE, 350, ResourceType.METAL, 300, ResourceType.GUNPOWDER, 150),
                    null));

    public static BuildingBlueprint of(BuildingType type) {
        return BLUEPRINTS.get(type);
    }

    public static Map<BuildingType, BuildingBlueprint> all() {
        return BLUEPRINTS;
    }
}
