package com.canarda.readiness.game.controller;

import com.canarda.readiness.game.dto.CollectCraftingJobRequest;
import com.canarda.readiness.game.dto.CraftingJobResponse;
import com.canarda.readiness.game.dto.CraftingRecipeResponse;
import com.canarda.readiness.game.dto.StartCraftingJobRequest;
import com.canarda.readiness.game.service.CraftingRecipe;
import com.canarda.readiness.game.service.CraftingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CraftingController {

    private final CraftingService craftingService;

    @GetMapping("/api/game/crafting/recipes")
    public List<CraftingRecipeResponse> recipes() {
        return CraftingRecipe.all().values().stream().map(CraftingRecipeResponse::from).toList();
    }

    @GetMapping("/api/game/buildings/{buildingId}/crafting-job")
    public ResponseEntity<CraftingJobResponse> activeJob(@PathVariable Long buildingId) {
        return craftingService.activeJob(buildingId)
                .map(job -> ResponseEntity.ok(CraftingJobResponse.from(job)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/api/game/buildings/{buildingId}/crafting-job")
    public CraftingJobResponse start(@PathVariable Long buildingId, @Valid @RequestBody StartCraftingJobRequest request) {
        return CraftingJobResponse.from(
                craftingService.startJob(buildingId, request.playerId(), request.equipmentType()));
    }

    @PostMapping("/api/game/buildings/{buildingId}/crafting-job/collect")
    public CraftingJobResponse collect(@PathVariable Long buildingId, @Valid @RequestBody CollectCraftingJobRequest request) {
        return CraftingJobResponse.from(craftingService.collect(buildingId, request.playerId()));
    }
}
