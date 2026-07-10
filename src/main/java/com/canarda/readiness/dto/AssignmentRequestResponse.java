package com.canarda.readiness.dto;

import com.canarda.readiness.domain.AssignmentRequest;
import com.canarda.readiness.domain.RequestStatus;

import java.time.LocalDateTime;

public record AssignmentRequestResponse(
        Long id,
        Long serviceMemberId,
        String serviceMemberName,
        Long fromUnitId,
        String fromUnitName,
        Long toUnitId,
        String toUnitName,
        String requestedRole,
        String requestedBy,
        RequestStatus status,
        LocalDateTime requestDate,
        LocalDateTime decisionDate,
        String decisionNotes) {

    public static AssignmentRequestResponse from(AssignmentRequest request) {
        return new AssignmentRequestResponse(
                request.getId(),
                request.getServiceMember().getId(),
                request.getServiceMember().getFirstName() + " " + request.getServiceMember().getLastName(),
                request.getFromUnit() != null ? request.getFromUnit().getId() : null,
                request.getFromUnit() != null ? request.getFromUnit().getName() : null,
                request.getToUnit().getId(),
                request.getToUnit().getName(),
                request.getRequestedRole(),
                request.getRequestedBy(),
                request.getStatus(),
                request.getRequestDate(),
                request.getDecisionDate(),
                request.getDecisionNotes());
    }
}
