package com.canarda.readiness.dto;

import com.canarda.readiness.domain.Branch;
import com.canarda.readiness.domain.Unit;

public record UnitResponse(
        Long id,
        String name,
        Branch branch,
        String location,
        double latitude,
        double longitude) {

    public static UnitResponse from(Unit unit) {
        return new UnitResponse(
                unit.getId(),
                unit.getName(),
                unit.getBranch(),
                unit.getLocation(),
                unit.getLatitude(),
                unit.getLongitude());
    }
}
