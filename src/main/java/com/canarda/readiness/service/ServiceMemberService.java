package com.canarda.readiness.service;

import com.canarda.readiness.domain.ReadinessStatus;
import com.canarda.readiness.domain.ServiceMember;
import com.canarda.readiness.domain.Unit;
import com.canarda.readiness.dto.CreateServiceMemberRequest;
import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.repository.ServiceMemberRepository;
import com.canarda.readiness.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceMemberService {

    private final ServiceMemberRepository serviceMemberRepository;
    private final UnitRepository unitRepository;

    public List<ServiceMember> findAll() {
        return serviceMemberRepository.findAll();
    }

    public ServiceMember findById(Long id) {
        return serviceMemberRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service member not found: " + id));
    }

    public List<ServiceMember> findByUnit(Long unitId) {
        return serviceMemberRepository.findByUnitId(unitId);
    }

    public List<ServiceMember> findByReadinessStatus(ReadinessStatus status) {
        return serviceMemberRepository.findByReadinessStatus(status);
    }

    public ServiceMember create(CreateServiceMemberRequest request) {
        Unit unit = unitRepository.findById(request.unitId())
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found: " + request.unitId()));

        ServiceMember member = ServiceMember.builder()
                .serviceNumber(request.serviceNumber())
                .firstName(request.firstName())
                .lastName(request.lastName())
                .rank(request.rank())
                .readinessStatus(request.readinessStatus())
                .unit(unit)
                .build();
        return serviceMemberRepository.save(member);
    }
}
