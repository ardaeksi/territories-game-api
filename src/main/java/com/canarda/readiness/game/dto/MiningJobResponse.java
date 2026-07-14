package com.canarda.readiness.game.dto;

import com.canarda.readiness.game.domain.MiningJob;
import com.canarda.readiness.game.domain.ResourceType;

import java.time.LocalDateTime;

public record MiningJobResponse(
        Long id,
        Long buildingId,
        ResourceType resourceType,
        LocalDateTime startedAt,
        int durationSeconds,
        int yieldAmount,
        LocalDateTime collectedAt) {

    public static MiningJobResponse from(MiningJob job) {
        return new MiningJobResponse(
                job.getId(),
                job.getBuilding().getId(),
                job.getResourceType(),
                job.getStartedAt(),
                job.getDurationSeconds(),
                job.getYieldAmount(),
                job.getCollectedAt());
    }
}
