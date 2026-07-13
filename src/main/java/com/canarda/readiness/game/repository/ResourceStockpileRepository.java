package com.canarda.readiness.game.repository;

import com.canarda.readiness.game.domain.ResourceStockpile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResourceStockpileRepository extends JpaRepository<ResourceStockpile, Long> {

    Optional<ResourceStockpile> findByPlayerId(Long playerId);
}
