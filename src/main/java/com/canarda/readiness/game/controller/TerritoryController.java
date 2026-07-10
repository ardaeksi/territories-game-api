package com.canarda.readiness.game.controller;

import com.canarda.readiness.game.dto.ClaimTerritoryRequest;
import com.canarda.readiness.game.dto.TerritoryResponse;
import com.canarda.readiness.game.service.TerritoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/game/territories")
@RequiredArgsConstructor
public class TerritoryController {

    private final TerritoryService territoryService;

    @GetMapping
    public List<TerritoryResponse> findAll(@RequestParam(required = false) Long ownerId) {
        var territories = ownerId != null ? territoryService.findByOwner(ownerId) : territoryService.findAll();
        return territories.stream().map(TerritoryResponse::from).toList();
    }

    @PostMapping("/{id}/claim")
    public TerritoryResponse claim(@PathVariable Long id, @Valid @RequestBody ClaimTerritoryRequest request) {
        return TerritoryResponse.from(territoryService.claim(id, request.playerId()));
    }
}
