package com.canarda.readiness.game.repository;

import com.canarda.readiness.game.domain.GameEquipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameEquipmentRepository extends JpaRepository<GameEquipment, Long> {

    Optional<GameEquipment> findByPlayerId(Long playerId);
}
