package com.canarda.readiness.repository;

import com.canarda.readiness.domain.AssignmentRequest;
import com.canarda.readiness.domain.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignmentRequestRepository extends JpaRepository<AssignmentRequest, Long> {

    List<AssignmentRequest> findByStatus(RequestStatus status);

    List<AssignmentRequest> findByToUnitId(Long unitId);

    List<AssignmentRequest> findByServiceMemberId(Long serviceMemberId);
}
