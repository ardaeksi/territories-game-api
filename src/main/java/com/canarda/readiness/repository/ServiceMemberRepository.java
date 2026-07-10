package com.canarda.readiness.repository;

import com.canarda.readiness.domain.ReadinessStatus;
import com.canarda.readiness.domain.ServiceMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceMemberRepository extends JpaRepository<ServiceMember, Long> {

    List<ServiceMember> findByUnitId(Long unitId);

    List<ServiceMember> findByReadinessStatus(ReadinessStatus readinessStatus);
}
