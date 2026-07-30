package com.canarda.readiness.game.service;

import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.game.domain.Building;
import com.canarda.readiness.game.domain.CraftingJob;
import com.canarda.readiness.game.domain.EquipmentType;
import com.canarda.readiness.game.repository.BuildingRepository;
import com.canarda.readiness.game.repository.CraftingJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CraftingService {

    private final CraftingJobRepository craftingJobRepository;
    private final BuildingRepository buildingRepository;
    private final GameResourceService gameResourceService;
    private final GameEquipmentService gameEquipmentService;

    @Transactional(readOnly = true)
    public Optional<CraftingJob> activeJob(Long buildingId) {
        return craftingJobRepository.findByBuildingIdAndCollectedAtIsNull(buildingId);
    }

    @Transactional
    public CraftingJob startJob(Long buildingId, Long playerId, EquipmentType equipmentType) {
        Building building = requireOwnedBuilding(buildingId, playerId);

        CraftingRecipe recipe = CraftingRecipe.of(equipmentType);
        if (recipe == null) {
            throw new IllegalStateException("Unknown equipment type: " + equipmentType);
        }
        if (building.getType() != recipe.requiredBuilding()) {
            throw new IllegalStateException(
                    "A " + recipe.requiredBuilding() + " is required to craft " + equipmentType);
        }
        if (craftingJobRepository.findByBuildingIdAndCollectedAtIsNull(buildingId).isPresent()) {
            throw new IllegalStateException("This factory already has a crafting job in progress");
        }

        gameResourceService.spend(playerId, recipe.cost());

        CraftingJob job = CraftingJob.builder()
                .building(building)
                .equipmentType(equipmentType)
                .startedAt(LocalDateTime.now())
                .durationSeconds(recipe.durationSeconds())
                .quantity(recipe.yieldQuantity())
                .build();
        return craftingJobRepository.save(job);
    }

    @Transactional
    public CraftingJob collect(Long buildingId, Long playerId) {
        requireOwnedBuilding(buildingId, playerId);

        CraftingJob job = craftingJobRepository.findByBuildingIdAndCollectedAtIsNull(buildingId)
                .orElseThrow(() -> new IllegalStateException("No crafting job in progress on this building"));

        LocalDateTime completesAt = job.getStartedAt().plusSeconds(job.getDurationSeconds());
        if (LocalDateTime.now().isBefore(completesAt)) {
            long remaining = ChronoUnit.SECONDS.between(LocalDateTime.now(), completesAt);
            throw new IllegalStateException("This job isn't done yet - " + remaining + "s remaining");
        }

        gameEquipmentService.credit(playerId, Map.of(job.getEquipmentType(), job.getQuantity()));
        job.setCollectedAt(LocalDateTime.now());
        return craftingJobRepository.save(job);
    }

    private Building requireOwnedBuilding(Long buildingId, Long playerId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResourceNotFoundException("Building not found: " + buildingId));

        if (building.getTerritory().getOwner() == null
                || !building.getTerritory().getOwner().getId().equals(playerId)) {
            throw new IllegalStateException("You don't own this building");
        }
        return building;
    }
}
