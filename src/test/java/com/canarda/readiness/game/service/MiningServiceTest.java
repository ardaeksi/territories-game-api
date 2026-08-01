package com.canarda.readiness.game.service;

import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.game.domain.Building;
import com.canarda.readiness.game.domain.BuildingType;
import com.canarda.readiness.game.domain.MiningJob;
import com.canarda.readiness.game.domain.Player;
import com.canarda.readiness.game.domain.ResourceType;
import com.canarda.readiness.game.domain.Territory;
import com.canarda.readiness.game.repository.BuildingRepository;
import com.canarda.readiness.game.repository.MiningJobRepository;
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
class MiningServiceTest {

    @Mock
    private MiningJobRepository miningJobRepository;
    @Mock
    private BuildingRepository buildingRepository;
    @Mock
    private GameResourceService gameResourceService;

    private MiningService miningService;

    private Building mine;

    @BeforeEach
    void setUp() {
        miningService = new MiningService(miningJobRepository, buildingRepository, gameResourceService);

        Player owner = Player.builder().id(6L).build();
        Territory ownedTerritory = Territory.builder().id(229L).owner(owner).build();
        mine = Building.builder().id(1L).territory(ownedTerritory).type(BuildingType.MINE).build();
    }

    @Test
    void startJob_rejectsResourceTypesOtherThanStoneOrMetal() {
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(mine));

        assertThatThrownBy(() -> miningService.startJob(1L, 6L, ResourceType.WOOD, "SHORT"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only Stone or Metal");
    }

    @Test
    void startJob_rejectsUnknownPreset() {
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(mine));
        when(miningJobRepository.findByBuildingIdAndCollectedAtIsNull(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> miningService.startJob(1L, 6L, ResourceType.STONE, "NOT_A_PRESET"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Unknown mining duration");
    }

    @Test
    void startJob_rejectsWhenAJobIsAlreadyInProgress() {
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(mine));
        when(miningJobRepository.findByBuildingIdAndCollectedAtIsNull(1L))
                .thenReturn(Optional.of(MiningJob.builder().build()));

        assertThatThrownBy(() -> miningService.startJob(1L, 6L, ResourceType.STONE, "SHORT"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("already has a job in progress");
    }

    @Test
    void startJob_rejectsNonMineBuilding() {
        Building oilRig = Building.builder().id(7L).territory(mine.getTerritory()).type(BuildingType.OIL_RIG).build();
        when(buildingRepository.findById(7L)).thenReturn(Optional.of(oilRig));

        assertThatThrownBy(() -> miningService.startJob(7L, 6L, ResourceType.STONE, "SHORT"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only a Mine");
    }

    @Test
    void startJob_rejectsNonOwner() {
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(mine));

        assertThatThrownBy(() -> miningService.startJob(1L, 999L, ResourceType.STONE, "SHORT"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("don't own this Mine");
    }

    @Test
    void startJob_rejectsUnknownBuilding() {
        when(buildingRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> miningService.startJob(1L, 6L, ResourceType.STONE, "SHORT"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void startJob_savesJobWithPresetDurationAndYield() {
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(mine));
        when(miningJobRepository.findByBuildingIdAndCollectedAtIsNull(1L)).thenReturn(Optional.empty());
        when(miningJobRepository.save(any(MiningJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MiningJob job = miningService.startJob(1L, 6L, ResourceType.METAL, "MEDIUM");

        MiningPreset preset = MiningPreset.of("MEDIUM");
        assertThat(job.getDurationSeconds()).isEqualTo(preset.durationSeconds());
        assertThat(job.getYieldAmount()).isEqualTo(preset.yieldByResource().get(ResourceType.METAL));
        assertThat(job.getResourceType()).isEqualTo(ResourceType.METAL);
    }

    @Test
    void collect_rejectsBeforeDurationElapses() {
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(mine));
        MiningJob job = MiningJob.builder()
                .building(mine)
                .resourceType(ResourceType.STONE)
                .startedAt(LocalDateTime.now())
                .durationSeconds(30)
                .yieldAmount(50)
                .build();
        when(miningJobRepository.findByBuildingIdAndCollectedAtIsNull(1L)).thenReturn(Optional.of(job));

        assertThatThrownBy(() -> miningService.collect(1L, 6L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("isn't done yet");

        verify(gameResourceService, never()).credit(any(), any());
    }

    @Test
    void collect_creditsYieldAndStampsCollectedAtOnceDurationElapsed() {
        when(buildingRepository.findById(1L)).thenReturn(Optional.of(mine));
        MiningJob job = MiningJob.builder()
                .building(mine)
                .resourceType(ResourceType.STONE)
                .startedAt(LocalDateTime.now().minusSeconds(60))
                .durationSeconds(30)
                .yieldAmount(50)
                .build();
        when(miningJobRepository.findByBuildingIdAndCollectedAtIsNull(1L)).thenReturn(Optional.of(job));
        when(miningJobRepository.save(any(MiningJob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        MiningJob collected = miningService.collect(1L, 6L);

        verify(gameResourceService).credit(eq(6L), eq(Map.of(ResourceType.STONE, 50)));
        assertThat(collected.getCollectedAt()).isNotNull();
    }
}
