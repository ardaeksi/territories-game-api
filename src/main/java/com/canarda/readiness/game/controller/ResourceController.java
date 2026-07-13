package com.canarda.readiness.game.controller;

import com.canarda.readiness.game.dto.ResourceStockpileResponse;
import com.canarda.readiness.game.service.GameResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game/players/{playerId}/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final GameResourceService gameResourceService;

    @GetMapping
    public ResourceStockpileResponse currentResources(@PathVariable Long playerId) {
        return ResourceStockpileResponse.of(playerId, gameResourceService.currentAmounts(playerId));
    }
}
