package com.canarda.readiness.game.service;

import com.canarda.readiness.game.domain.ResourceType;

import java.util.LinkedHashMap;
import java.util.Map;

/** A selectable mining job length and its fixed resource payout - static data, same as BuildingBlueprint. */
public record MiningPreset(
        String key,
        String label,
        int durationSeconds,
        Map<ResourceType, Integer> yieldByResource) {

    private static final Map<String, MiningPreset> PRESETS = new LinkedHashMap<>();

    static {
        register(new MiningPreset("SHORT", "Short", 30,
                Map.of(ResourceType.STONE, 50, ResourceType.METAL, 25)));
        register(new MiningPreset("MEDIUM", "Medium", 120,
                Map.of(ResourceType.STONE, 150, ResourceType.METAL, 80)));
        register(new MiningPreset("LONG", "Long", 300,
                Map.of(ResourceType.STONE, 300, ResourceType.METAL, 175)));
    }

    private static void register(MiningPreset preset) {
        PRESETS.put(preset.key(), preset);
    }

    public static MiningPreset of(String key) {
        return PRESETS.get(key);
    }

    public static Map<String, MiningPreset> all() {
        return PRESETS;
    }
}
