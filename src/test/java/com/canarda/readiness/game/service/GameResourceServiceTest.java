package com.canarda.readiness.game.service;

import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.game.domain.BuildingType;
import com.canarda.readiness.game.domain.Player;
import com.canarda.readiness.game.domain.ResourceStockpile;
import com.canarda.readiness.game.domain.ResourceType;
import com.canarda.readiness.game.repository.BuildingRepository;
import com.canarda.readiness.game.repository.PlayerRepository;
import com.canarda.readiness.game.repository.ResourceStockpileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameResourceServiceTest {

    @Mock
    private ResourceStockpileRepository stockpileRepository;
    @Mock
    private PlayerRepository playerRepository;
    @Mock
    private BuildingRepository buildingRepository;

    private GameResourceService gameResourceService;

    @BeforeEach
    void setUp() {
        gameResourceService = new GameResourceService(stockpileRepository, playerRepository, buildingRepository);
        lenient().when(stockpileRepository.save(any(ResourceStockpile.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    private ResourceStockpile stockpileWith(Map<ResourceType, Integer> amounts) {
        ResourceStockpile stockpile = ResourceStockpile.builder()
                .player(Player.builder().id(6L).build())
                .amounts(new HashMap<>(amounts))
                .build();
        when(stockpileRepository.findByPlayerId(6L)).thenReturn(Optional.of(stockpile));
        return stockpile;
    }

    @Test
    void canSpend_alwaysTrueForWoodStoneFood() {
        assertThat(gameResourceService.canSpend(6L, ResourceType.WOOD)).isTrue();
        assertThat(gameResourceService.canSpend(6L, ResourceType.STONE)).isTrue();
        assertThat(gameResourceService.canSpend(6L, ResourceType.FOOD)).isTrue();
    }

    @Test
    void canSpend_gatedResourcesRequireTheirExtractionBuilding() {
        when(buildingRepository.existsByTerritoryOwnerIdAndType(6L, BuildingType.MINE)).thenReturn(true);
        when(buildingRepository.existsByTerritoryOwnerIdAndType(6L, BuildingType.OIL_RIG)).thenReturn(false);

        assertThat(gameResourceService.canSpend(6L, ResourceType.METAL)).isTrue();
        assertThat(gameResourceService.canSpend(6L, ResourceType.OIL)).isFalse();
    }

    @Test
    void spend_deductsWhenGateAndBalanceBothOk() {
        ResourceStockpile stockpile = stockpileWith(Map.of(ResourceType.WOOD, 500, ResourceType.STONE, 500));

        gameResourceService.spend(6L, Map.of(ResourceType.WOOD, 200, ResourceType.STONE, 100));

        assertThat(stockpile.getAmounts())
                .containsEntry(ResourceType.WOOD, 300)
                .containsEntry(ResourceType.STONE, 400);
    }

    @Test
    void spend_rejectsWhenExtractionBuildingMissing_andDeductsNothing() {
        ResourceStockpile stockpile = stockpileWith(Map.of(ResourceType.METAL, 1000));
        when(buildingRepository.existsByTerritoryOwnerIdAndType(6L, BuildingType.MINE)).thenReturn(false);

        assertThatThrownBy(() -> gameResourceService.spend(6L, Map.of(ResourceType.METAL, 100)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("extraction building hasn't been built");

        assertThat(stockpile.getAmounts()).containsEntry(ResourceType.METAL, 1000);
    }

    @Test
    void spend_rejectsInsufficientBalance_andDeductsNothing() {
        ResourceStockpile stockpile = stockpileWith(Map.of(ResourceType.WOOD, 50));

        assertThatThrownBy(() -> gameResourceService.spend(6L, Map.of(ResourceType.WOOD, 200)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Not enough WOOD");

        assertThat(stockpile.getAmounts()).containsEntry(ResourceType.WOOD, 50);
    }

    @Test
    void spend_isAllOrNothingAcrossMultipleCostLines() {
        ResourceStockpile stockpile = stockpileWith(Map.of(ResourceType.WOOD, 500, ResourceType.STONE, 10));

        assertThatThrownBy(() -> gameResourceService.spend(6L,
                Map.of(ResourceType.WOOD, 200, ResourceType.STONE, 100)))
                .isInstanceOf(IllegalStateException.class);

        assertThat(stockpile.getAmounts()).containsEntry(ResourceType.WOOD, 500);
    }

    @Test
    void credit_mergesAdditively() {
        ResourceStockpile stockpile = stockpileWith(new HashMap<>(Map.of(ResourceType.WOOD, 100)));

        gameResourceService.credit(6L, Map.of(ResourceType.WOOD, 50, ResourceType.STONE, 20));

        assertThat(stockpile.getAmounts())
                .containsEntry(ResourceType.WOOD, 150)
                .containsEntry(ResourceType.STONE, 20);
    }

    @Test
    void currentAmounts_lazilyCreatesStockpileForFirstTimePlayer() {
        when(stockpileRepository.findByPlayerId(6L)).thenReturn(Optional.empty());
        when(playerRepository.findById(6L)).thenReturn(Optional.of(Player.builder().id(6L).build()));

        assertThat(gameResourceService.currentAmounts(6L)).isEmpty();
    }

    @Test
    void currentAmounts_rejectsUnknownPlayer() {
        when(stockpileRepository.findByPlayerId(6L)).thenReturn(Optional.empty());
        when(playerRepository.findById(6L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameResourceService.currentAmounts(6L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
