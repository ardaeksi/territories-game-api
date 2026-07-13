package com.canarda.readiness.game.repository;

import com.canarda.readiness.game.domain.Building;
import com.canarda.readiness.game.domain.BuildingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BuildingRepository extends JpaRepository<Building, Long> {

    List<Building> findByTerritoryId(Long territoryId);

    List<Building> findByTerritoryOwnerId(Long ownerId);

    boolean existsByTerritoryOwnerIdAndType(Long ownerId, BuildingType type);

    boolean existsByTerritoryIdAndType(Long territoryId, BuildingType type);
}
