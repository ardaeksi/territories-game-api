package com.canarda.readiness.game.repository;

import com.canarda.readiness.game.domain.GameSession;
import com.canarda.readiness.game.domain.GameSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    Optional<GameSession> findFirstByStatus(GameSessionStatus status);
}
