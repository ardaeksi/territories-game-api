package com.canarda.readiness.game.repository;

import com.canarda.readiness.game.domain.MiningJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MiningJobRepository extends JpaRepository<MiningJob, Long> {

    Optional<MiningJob> findByBuildingIdAndCollectedAtIsNull(Long buildingId);
}
