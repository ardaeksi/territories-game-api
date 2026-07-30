package com.canarda.readiness.game.dto;

import com.canarda.readiness.game.domain.BuildingType;
import com.canarda.readiness.game.domain.EquipmentType;
import com.canarda.readiness.game.domain.ResourceType;
import com.canarda.readiness.game.service.CraftingRecipe;

import java.util.Map;

public record CraftingRecipeResponse(
        EquipmentType equipmentType,
        BuildingType requiredBuilding,
        Map<ResourceType, Integer> cost,
        int durationSeconds,
        int yieldQuantity) {

    public static CraftingRecipeResponse from(CraftingRecipe recipe) {
        return new CraftingRecipeResponse(
                recipe.equipmentType(),
                recipe.requiredBuilding(),
                recipe.cost(),
                recipe.durationSeconds(),
                recipe.yieldQuantity());
    }
}
