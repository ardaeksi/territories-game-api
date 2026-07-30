package com.canarda.readiness.game.repository;

import com.canarda.readiness.game.domain.CraftingJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CraftingJobRepository extends JpaRepository<CraftingJob, Long> {

    Optional<CraftingJob> findByBuildingIdAndCollectedAtIsNull(Long buildingId);
}
