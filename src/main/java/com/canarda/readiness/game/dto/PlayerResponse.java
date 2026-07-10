package com.canarda.readiness.game.dto;

import com.canarda.readiness.game.domain.Player;

public record PlayerResponse(Long id, String displayName, String colorHex) {

    public static PlayerResponse from(Player player) {
        return new PlayerResponse(player.getId(), player.getDisplayName(), player.getColorHex());
    }
}
