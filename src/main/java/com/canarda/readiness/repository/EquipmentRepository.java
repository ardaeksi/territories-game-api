package com.canarda.readiness.repository;

import com.canarda.readiness.domain.Equipment;
import com.canarda.readiness.domain.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {

    List<Equipment> findByUnitId(Long unitId);

    List<Equipment> findByType(EquipmentType type);
}
