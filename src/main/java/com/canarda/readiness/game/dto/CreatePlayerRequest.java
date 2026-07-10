package com.canarda.readiness.game.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePlayerRequest(@NotBlank String displayName) {
}
