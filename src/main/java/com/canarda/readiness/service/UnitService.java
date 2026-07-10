package com.canarda.readiness.service;

import com.canarda.readiness.domain.Unit;
import com.canarda.readiness.dto.CreateUnitRequest;
import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UnitService {

    private final UnitRepository unitRepository;

    public List<Unit> findAll() {
        return unitRepository.findAll();
    }

    public Unit findById(Long id) {
        return unitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found: " + id));
    }

    public Unit create(CreateUnitRequest request) {
        Unit unit = Unit.builder()
                .name(request.name())
                .branch(request.branch())
                .location(request.location())
                .latitude(request.latitude())
                .longitude(request.longitude())
                .build();
        return unitRepository.save(unit);
    }
}
