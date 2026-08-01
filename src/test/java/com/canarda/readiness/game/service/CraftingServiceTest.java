package com.canarda.readiness.game.service;

import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.game.domain.Building;
import com.canarda.readiness.game.domain.BuildingType;
import com.canarda.readiness.game.domain.CraftingJob;
import com.canarda.readiness.game.domain.EquipmentType;
import com.canarda.readiness.game.domain.Player;
import com.canarda.readiness.game.domain.Territory;
import com.canarda.readiness.game.repository.BuildingRepository;
import com.canarda.readiness.game.repository.CraftingJobRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CraftingServiceTest {

    @Mock
    private CraftingJobRepository craftingJobRepository;
    @Mock
    private BuildingRepository buildingRepository;
    @Mock
    private GameResourceService gameResourceService;
    @Mock
    private GameEquipmentService gameEquipmentService;

    private CraftingService craftingService;

    private Player owner;
    private Building boatFactory;
    private Building armyFactory;

    @BeforeEach
    void setUp() {
        craftingService = new CraftingService(
                craftingJobRepository, buildingRepository, gameResourceService, gameEquipmentService);

        owner = Player.builder().id(6L).build();
        Territory ownedTerritory = Territory.builder().id(229L).owner(owner).build();
        boatFactory = Building.builder().id(2L).territory(ownedTerritory).type(BuildingType.BOAT_FACTORY).build();
        armyFactory = Building.builder().id(18L).territory(ownedTerritory).type(BuildingType.ARMY_FACTORY).build();
    }

    @Test
    void startJob_spendsRecipeCostAndSavesJob() {
        when(buildingRepository.findById(2L)).thenReturn(Optional.of(boatFactory));
        when(craftingJobRepository.findByBuildingIdAndCollectedAtIsNull(2L)).thenReturn(Optional.empty());
        when(craftingJobRepository.save(any(CraftingJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CraftingJob job = craftingService.startJob(2L, 6L, EquipmentType.SHIP);

        CraftingRecipe recipe = CraftingRecipe.of(EquipmentType.SHIP);
        verify(gameResourceService).spend(6L, recipe.cost());
        assertThat(job.getBuilding()).isEqualTo(boatFactory);
        assertThat(job.getEquipmentType()).isEqualTo(EquipmentType.SHIP);
        assertThat(job.getDurationSeconds()).isEqualTo(recipe.durationSeconds());
        assertThat(job.getQuantity()).isEqualTo(recipe.yieldQuantity());
        assertThat(job.getCollectedAt()).isNull();
    }

    @Test
    void startJob_rejectsWrongBuildingTypeForRecipe() {
        when(buildingRepository.findById(2L)).thenReturn(Optional.of(boatFactory));

        assertThatThrownBy(() -> craftingService.startJob(2L, 6L, EquipmentType.TANK))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ARMY_FACTORY");

        verify(gameResourceService, never()).spend(any(), any());
        verify(craftingJobRepository, never()).save(any());
    }

    @Test
    void startJob_rejectsWhenBuildingAlreadyHasAJobInProgress() {
        when(buildingRepository.findById(2L)).thenReturn(Optional.of(boatFactory));
        when(craftingJobRepository.findByBuildingIdAndCollectedAtIsNull(2L))
                .thenReturn(Optional.of(CraftingJob.builder().build()));

        assertThatThrownBy(() -> craftingService.startJob(2L, 6L, EquipmentType.SHIP))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already has a crafting job in progress");

        verify(gameResourceService, never()).spend(any(), any());
    }

    @Test
    void startJob_rejectsNonOwner() {
        when(buildingRepository.findById(2L)).thenReturn(Optional.of(boatFactory));

        assertThatThrownBy(() -> craftingService.startJob(2L, 999L, EquipmentType.SHIP))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("don't own this building");
    }

    @Test
    void startJob_rejectsUnknownBuilding() {
        when(buildingRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> craftingService.startJob(2L, 6L, EquipmentType.SHIP))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void collect_rejectsBeforeDurationElapses() {
        when(buildingRepository.findById(18L)).thenReturn(Optional.of(armyFactory));
        CraftingJob job = CraftingJob.builder()
                .building(armyFactory)
                .equipmentType(EquipmentType.GUN)
                .startedAt(LocalDateTime.now())
                .durationSeconds(90)
                .quantity(1)
                .build();
        when(craftingJobRepository.findByBuildingIdAndCollectedAtIsNull(18L)).thenReturn(Optional.of(job));

        assertThatThrownBy(() -> craftingService.collect(18L, 6L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("isn't done yet");

        verify(gameEquipmentService, never()).credit(any(), any());
    }

    @Test
    void collect_creditsEquipmentAndStampsCollectedAtOnceDurationElapsed() {
        when(buildingRepository.findById(18L)).thenReturn(Optional.of(armyFactory));
        CraftingJob job = CraftingJob.builder()
                .building(armyFactory)
                .equipmentType(EquipmentType.GUN)
                .startedAt(LocalDateTime.now().minusSeconds(200))
                .durationSeconds(90)
                .quantity(1)
                .build();
        when(craftingJobRepository.findByBuildingIdAndCollectedAtIsNull(18L)).thenReturn(Optional.of(job));
        when(craftingJobRepository.save(any(CraftingJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CraftingJob collected = craftingService.collect(18L, 6L);

        verify(gameEquipmentService).credit(eq(6L), eq(Map.of(EquipmentType.GUN, 1)));
        assertThat(collected.getCollectedAt()).isNotNull();
    }

    @Test
    void activeJob_delegatesToRepository() {
        CraftingJob job = CraftingJob.builder().build();
        when(craftingJobRepository.findByBuildingIdAndCollectedAtIsNull(2L)).thenReturn(Optional.of(job));

        assertThat(craftingService.activeJob(2L)).contains(job);
    }
}
