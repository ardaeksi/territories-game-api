package com.canarda.readiness.game.dto;

import com.canarda.readiness.game.domain.BuildingType;
import com.canarda.readiness.game.domain.ResourceType;
import com.canarda.readiness.game.service.BuildingBlueprint;

import java.util.Map;

public record BuildingBlueprintResponse(
        BuildingType type,
        Map<ResourceType, Integer> cost,
        ResourceType unlocksSpendingOf) {

    public static BuildingBlueprintResponse from(BuildingBlueprint blueprint) {
        return new BuildingBlueprintResponse(blueprint.type(), blueprint.cost(), blueprint.unlocksSpendingOf());
    }
}
