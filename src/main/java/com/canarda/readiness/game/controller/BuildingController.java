package com.canarda.readiness.game.controller;

import com.canarda.readiness.game.dto.BuildingBlueprintResponse;
import com.canarda.readiness.game.dto.BuildingResponse;
import com.canarda.readiness.game.dto.CreateBuildingRequest;
import com.canarda.readiness.game.service.BuildingBlueprint;
import com.canarda.readiness.game.service.BuildingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;

    @GetMapping("/api/game/buildings/blueprints")
    public List<BuildingBlueprintResponse> blueprints() {
        return BuildingBlueprint.all().values().stream().map(BuildingBlueprintResponse::from).toList();
    }

    @GetMapping("/api/game/territories/{territoryId}/buildings")
    public List<BuildingResponse> findByTerritory(@PathVariable Long territoryId) {
        return buildingService.findByTerritory(territoryId).stream().map(BuildingResponse::from).toList();
    }

    @PostMapping("/api/game/territories/{territoryId}/buildings")
    public ResponseEntity<BuildingResponse> construct(
            @PathVariable Long territoryId,
            @Valid @RequestBody CreateBuildingRequest request) {
        BuildingResponse response = BuildingResponse.from(
                buildingService.construct(territoryId, request.playerId(), request.type()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
