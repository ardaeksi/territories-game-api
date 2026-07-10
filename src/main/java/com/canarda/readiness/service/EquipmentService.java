package com.canarda.readiness.service;

import com.canarda.readiness.domain.Equipment;
import com.canarda.readiness.domain.Unit;
import com.canarda.readiness.dto.CreateEquipmentRequest;
import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.repository.EquipmentRepository;
import com.canarda.readiness.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final UnitRepository unitRepository;

    public List<Equipment> findAll() {
        return equipmentRepository.findAll();
    }

    public Equipment findById(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Equipment not found: " + id));
    }

    public List<Equipment> findByUnit(Long unitId) {
        return equipmentRepository.findByUnitId(unitId);
    }

    public Equipment create(CreateEquipmentRequest request) {
        Unit unit = unitRepository.findById(request.unitId())
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found: " + request.unitId()));

        Equipment equipment = Equipment.builder()
                .name(request.name())
                .type(request.type())
                .quantity(request.quantity())
                .status(request.status())
                .unit(unit)
                .build();
        return equipmentRepository.save(equipment);
    }
}
