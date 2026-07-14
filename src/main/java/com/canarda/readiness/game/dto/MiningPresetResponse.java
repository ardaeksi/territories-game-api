package com.canarda.readiness.game.dto;

import com.canarda.readiness.game.domain.ResourceType;
import com.canarda.readiness.game.service.MiningPreset;

import java.util.Map;

public record MiningPresetResponse(
        String key,
        String label,
        int durationSeconds,
        Map<ResourceType, Integer> yieldByResource) {

    public static MiningPresetResponse from(MiningPreset preset) {
        return new MiningPresetResponse(preset.key(), preset.label(), preset.durationSeconds(), preset.yieldByResource());
    }
}
