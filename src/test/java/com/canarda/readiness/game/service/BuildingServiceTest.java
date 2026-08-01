package com.canarda.readiness.game.service;

import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.game.domain.Building;
import com.canarda.readiness.game.domain.BuildingType;
import com.canarda.readiness.game.domain.Player;
import com.canarda.readiness.game.domain.Territory;
import com.canarda.readiness.game.repository.BuildingRepository;
import com.canarda.readiness.game.repository.TerritoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuildingServiceTest {

    @Mock
    private BuildingRepository buildingRepository;
    @Mock
    private TerritoryRepository territoryRepository;
    @Mock
    private GameResourceService gameResourceService;

    private BuildingService buildingService;

    private Territory ownedTerritory;

    @BeforeEach
    void setUp() {
        buildingService = new BuildingService(buildingRepository, territoryRepository, gameResourceService);
        ownedTerritory = Territory.builder().id(229L).owner(Player.builder().id(6L).build()).countryName("Senegal").build();
    }

    @Test
    void construct_spendsBlueprintCostAndSavesBuilding() {
        when(territoryRepository.findById(229L)).thenReturn(Optional.of(ownedTerritory));
        when(buildingRepository.existsByTerritoryIdAndType(229L, BuildingType.MINE)).thenReturn(false);
        when(buildingRepository.save(any(Building.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Building building = buildingService.construct(229L, 6L, BuildingType.MINE);

        BuildingBlueprint blueprint = BuildingBlueprint.of(BuildingType.MINE);
        verify(gameResourceService).spend(6L, blueprint.cost());
        assertThat(building.getTerritory()).isEqualTo(ownedTerritory);
        assertThat(building.getType()).isEqualTo(BuildingType.MINE);
        assertThat(building.getBuiltAt()).isNotNull();
    }

    @Test
    void construct_rejectsDuplicateBuildingTypeOnTerritory() {
        when(territoryRepository.findById(229L)).thenReturn(Optional.of(ownedTerritory));
        when(buildingRepository.existsByTerritoryIdAndType(229L, BuildingType.MINE)).thenReturn(true);

        assertThatThrownBy(() -> buildingService.construct(229L, 6L, BuildingType.MINE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already exists on Senegal");

        verify(gameResourceService, never()).spend(any(), any());
    }

    @Test
    void construct_rejectsNonOwner() {
        when(territoryRepository.findById(229L)).thenReturn(Optional.of(ownedTerritory));

        assertThatThrownBy(() -> buildingService.construct(229L, 999L, BuildingType.MINE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("don't own this territory");
    }

    @Test
    void construct_rejectsUnclaimedTerritory() {
        Territory unclaimed = Territory.builder().id(500L).owner(null).build();
        when(territoryRepository.findById(500L)).thenReturn(Optional.of(unclaimed));

        assertThatThrownBy(() -> buildingService.construct(500L, 6L, BuildingType.MINE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("don't own this territory");
    }

    @Test
    void construct_rejectsUnknownTerritory() {
        when(territoryRepository.findById(229L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> buildingService.construct(229L, 6L, BuildingType.MINE))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
