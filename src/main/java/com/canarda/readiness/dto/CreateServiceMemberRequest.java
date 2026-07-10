package com.canarda.readiness.dto;

import com.canarda.readiness.domain.ReadinessStatus;
import com.canarda.readiness.domain.Rank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateServiceMemberRequest(
        @NotBlank String serviceNumber,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotNull Rank rank,
        @NotNull ReadinessStatus readinessStatus,
        @NotNull Long unitId) {
}
