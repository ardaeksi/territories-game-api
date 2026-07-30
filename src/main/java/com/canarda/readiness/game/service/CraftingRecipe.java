package com.canarda.readiness.game.service;

import com.canarda.readiness.game.domain.BuildingType;
import com.canarda.readiness.game.domain.EquipmentType;
import com.canarda.readiness.game.domain.ResourceType;
import java.util.Map;

/** What a factory can craft: cost, build time, and yield - static data, same as BuildingBlueprint/MiningPreset. */
public record CraftingRecipe(
        EquipmentType equipmentType,
        BuildingType requiredBuilding,
        Map<ResourceType, Integer> cost,
        int durationSeconds,
        int yieldQuantity) {

    private static final Map<EquipmentType, CraftingRecipe> RECIPES = Map.of(
            EquipmentType.SHIP, new CraftingRecipe(EquipmentType.SHIP, BuildingType.BOAT_FACTORY,
                    Map.of(ResourceType.WOOD, 300, ResourceType.METAL, 200), 180, 1),
            EquipmentType.GUN, new CraftingRecipe(EquipmentType.GUN, BuildingType.ARMY_FACTORY,
                    Map.of(ResourceType.METAL, 150, ResourceType.GUNPOWDER, 100), 90, 1),
            EquipmentType.TANK, new CraftingRecipe(EquipmentType.TANK, BuildingType.ARMY_FACTORY,
                    Map.of(ResourceType.METAL, 400, ResourceType.GUNPOWDER, 250, ResourceType.OIL, 100), 300, 1));

    public static CraftingRecipe of(EquipmentType equipmentType) { return RECIPES.get(equipmentType); }
    public static Map<EquipmentType, CraftingRecipe> all() { return RECIPES; }
}
