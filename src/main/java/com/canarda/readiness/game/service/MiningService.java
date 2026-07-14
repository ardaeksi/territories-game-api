package com.canarda.readiness.game.service;

import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.game.domain.Building;
import com.canarda.readiness.game.domain.BuildingType;
import com.canarda.readiness.game.domain.MiningJob;
import com.canarda.readiness.game.domain.ResourceType;
import com.canarda.readiness.game.repository.BuildingRepository;
import com.canarda.readiness.game.repository.MiningJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MiningService {

    private final MiningJobRepository miningJobRepository;
    private final BuildingRepository buildingRepository;
    private final GameResourceService gameResourceService;

    @Transactional(readOnly = true)
    public Optional<MiningJob> activeJob(Long buildingId) {
        return miningJobRepository.findByBuildingIdAndCollectedAtIsNull(buildingId);
    }

    @Transactional
    public MiningJob startJob(Long buildingId, Long playerId, ResourceType resourceType, String presetKey) {
        Building building = requireOwnedMine(buildingId, playerId);

        if (resourceType != ResourceType.STONE && resourceType != ResourceType.METAL) {
            throw new IllegalStateException("Only Stone or Metal can be mined right now");
        }

        if (miningJobRepository.findByBuildingIdAndCollectedAtIsNull(buildingId).isPresent()) {
            throw new IllegalStateException("This Mine already has a job in progress");
        }

        MiningPreset preset = MiningPreset.of(presetKey);
        if (preset == null) {
            throw new IllegalStateException("Unknown mining duration: " + presetKey);
        }

        MiningJob job = MiningJob.builder()
                .building(building)
                .resourceType(resourceType)
                .startedAt(LocalDateTime.now())
                .durationSeconds(preset.durationSeconds())
                .yieldAmount(preset.yieldByResource().get(resourceType))
                .build();
        return miningJobRepository.save(job);
    }

    @Transactional
    public MiningJob collect(Long buildingId, Long playerId) {
        requireOwnedMine(buildingId, playerId);

        MiningJob job = miningJobRepository.findByBuildingIdAndCollectedAtIsNull(buildingId)
                .orElseThrow(() -> new IllegalStateException("No mining job in progress on this Mine"));

        LocalDateTime completesAt = job.getStartedAt().plusSeconds(job.getDurationSeconds());
        if (LocalDateTime.now().isBefore(completesAt)) {
            long remaining = ChronoUnit.SECONDS.between(LocalDateTime.now(), completesAt);
            throw new IllegalStateException("This job isn't done yet - " + remaining + "s remaining");
        }

        gameResourceService.credit(playerId, Map.of(job.getResourceType(), job.getYieldAmount()));
        job.setCollectedAt(LocalDateTime.now());
        return miningJobRepository.save(job);
    }

    private Building requireOwnedMine(Long buildingId, Long playerId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found: " + buildingId));

        if (building.getType() != BuildingType.MINE) {
            throw new IllegalStateException("Only a Mine can run mining jobs");
        }
        if (building.getTerritory().getOwner() == null
                || !building.getTerritory().getOwner().getId().equals(playerId)) {
            throw new IllegalStateException("You don't own this Mine");
        }
        return building;
    }
}
