package com.canarda.readiness.dto;

import com.canarda.readiness.domain.Equipment;
import com.canarda.readiness.domain.EquipmentStatus;
import com.canarda.readiness.domain.EquipmentType;

public record EquipmentResponse(
        Long id,
        String name,
        EquipmentType type,
        int quantity,
        EquipmentStatus status,
        Long unitId,
        String unitName) {

    public static EquipmentResponse from(Equipment equipment) {
        return new EquipmentResponse(
                equipment.getId(),
                equipment.getName(),
                equipment.getType(),
                equipment.getQuantity(),
                equipment.getStatus(),
                equipment.getUnit() != null ? equipment.getUnit().getId() : null,
                equipment.getUnit() != null ? equipment.getUnit().getName() : null);
    }
}
