package com.canarda.readiness.dto;

import com.canarda.readiness.domain.ReadinessStatus;
import com.canarda.readiness.domain.Rank;
import com.canarda.readiness.domain.ServiceMember;

public record ServiceMemberResponse(
        Long id,
        String serviceNumber,
        String firstName,
        String lastName,
        Rank rank,
        ReadinessStatus readinessStatus,
        Long unitId,
        String unitName) {

    public static ServiceMemberResponse from(ServiceMember member) {
        return new ServiceMemberResponse(
                member.getId(),
                member.getServiceNumber(),
                member.getFirstName(),
                member.getLastName(),
                member.getRank(),
                member.getReadinessStatus(),
                member.getUnit() != null ? member.getUnit().getId() : null,
                member.getUnit() != null ? member.getUnit().getName() : null);
    }
}
