package com.canarda.readiness.game.dto;

import com.canarda.readiness.game.domain.CraftingJob;
import com.canarda.readiness.game.domain.EquipmentType;

import java.time.LocalDateTime;

public record CraftingJobResponse(
        Long id,
        Long buildingId,
        EquipmentType equipmentType,
        LocalDateTime startedAt,
        int durationSeconds,
        int quantity,
        LocalDateTime collectedAt) {

    public static CraftingJobResponse from(CraftingJob job) {
        return new CraftingJobResponse(
                job.getId(),
                job.getBuilding().getId(),
                job.getEquipmentType(),
                job.getStartedAt(),
                job.getDurationSeconds(),
                job.getQuantity(),
                job.getCollectedAt());
    }
}
