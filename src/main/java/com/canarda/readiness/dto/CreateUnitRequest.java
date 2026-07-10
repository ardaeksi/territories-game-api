package com.canarda.readiness.dto;

import com.canarda.readiness.domain.Branch;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUnitRequest(
        @NotBlank String name,
        @NotNull Branch branch,
        @NotBlank String location,
        @DecimalMin("-90.0") @DecimalMax("90.0") double latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") double longitude) {
}
