package com.canarda.readiness.dto;

import com.canarda.readiness.domain.EquipmentStatus;
import com.canarda.readiness.domain.EquipmentType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateEquipmentRequest(
        @NotBlank String name,
        @NotNull EquipmentType type,
        @Min(0) int quantity,
        @NotNull EquipmentStatus status,
        @NotNull Long unitId) {
}
