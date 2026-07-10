package com.canarda.readiness.game.repository;

import com.canarda.readiness.game.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
