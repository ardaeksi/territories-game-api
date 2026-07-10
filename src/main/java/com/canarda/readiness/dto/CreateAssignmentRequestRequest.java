package com.canarda.readiness.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateAssignmentRequestRequest(
        @NotNull Long serviceMemberId,
        @NotNull Long toUnitId,
        @NotBlank String requestedRole,
        @NotBlank String requestedBy) {
}
