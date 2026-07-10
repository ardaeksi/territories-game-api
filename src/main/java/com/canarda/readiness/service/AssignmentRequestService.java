package com.canarda.readiness.service;

import com.canarda.readiness.domain.AssignmentRequest;
import com.canarda.readiness.domain.RequestStatus;
import com.canarda.readiness.domain.ServiceMember;
import com.canarda.readiness.domain.Unit;
import com.canarda.readiness.dto.CreateAssignmentRequestRequest;
import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.repository.AssignmentRequestRepository;
import com.canarda.readiness.repository.ServiceMemberRepository;
import com.canarda.readiness.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentRequestService {

    private final AssignmentRequestRepository assignmentRequestRepository;
    private final ServiceMemberRepository serviceMemberRepository;
    private final UnitRepository unitRepository;

    public List<AssignmentRequest> findAll() {
        return assignmentRequestRepository.findAll();
    }

    public List<AssignmentRequest> findByStatus(RequestStatus status) {
        return assignmentRequestRepository.findByStatus(status);
    }

    public AssignmentRequest findById(Long id) {
        return assignmentRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment request not found: " + id));
    }

    public AssignmentRequest create(CreateAssignmentRequestRequest request) {
        ServiceMember member = serviceMemberRepository.findById(request.serviceMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Service member not found: " + request.serviceMemberId()));
        Unit toUnit = unitRepository.findById(request.toUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found: " + request.toUnitId()));

        AssignmentRequest assignmentRequest = AssignmentRequest.builder()
                .serviceMember(member)
                .fromUnit(member.getUnit())
                .toUnit(toUnit)
                .requestedRole(request.requestedRole())
                .requestedBy(request.requestedBy())
                .status(RequestStatus.PENDING)
                .requestDate(LocalDateTime.now())
                .build();

        return assignmentRequestRepository.save(assignmentRequest);
    }

    public AssignmentRequest approve(Long id, String notes) {
        AssignmentRequest request = requirePending(id);

        ServiceMember member = request.getServiceMember();
        member.setUnit(request.getToUnit());
        serviceMemberRepository.save(member);

        request.setStatus(RequestStatus.COMPLETED);
        request.setDecisionDate(LocalDateTime.now());
        request.setDecisionNotes(notes);
        return assignmentRequestRepository.save(request);
    }

    public AssignmentRequest deny(Long id, String notes) {
        AssignmentRequest request = requirePending(id);

        request.setStatus(RequestStatus.DENIED);
        request.setDecisionDate(LocalDateTime.now());
        request.setDecisionNotes(notes);
        return assignmentRequestRepository.save(request);
    }

    private AssignmentRequest requirePending(Long id) {
        AssignmentRequest request = findById(id);
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("Assignment request " + id + " is not PENDING (current status: " + request.getStatus() + ")");
        }
        return request;
    }
}
