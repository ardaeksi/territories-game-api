package com.canarda.readiness.game.service;

import com.canarda.readiness.game.domain.GameSession;
import com.canarda.readiness.game.domain.GameSessionStatus;
import com.canarda.readiness.game.domain.Player;
import com.canarda.readiness.game.domain.Territory;
import com.canarda.readiness.game.repository.GameSessionRepository;
import com.canarda.readiness.game.repository.PlayerRepository;
import com.canarda.readiness.game.repository.TerritoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PlayerService {

    private static final String[] PLAYER_COLORS = {
            "#e74c3c", "#3498db", "#2ecc71", "#f1c40f", "#9b59b6", "#e67e22", "#1abc9c", "#e84393"
    };

    private final PlayerRepository playerRepository;
    private final GameSessionRepository gameSessionRepository;
    private final TerritoryRepository territoryRepository;
    private final GameResourceService gameResourceService;

    public Player create(String displayName) {
        GameSession session = gameSessionRepository.findFirstByStatus(GameSessionStatus.ACTIVE)
                .orElseGet(this::createSession);

        long existingPlayerCount = playerRepository.count();
        String color = PLAYER_COLORS[(int) (existingPlayerCount % PLAYER_COLORS.length)];

        Player player = playerRepository.save(Player.builder()
                .gameSession(session)
                .displayName(displayName)
                .colorHex(color)
                .joinedAt(LocalDateTime.now())
                .build());

        assignStartingTerritory(player);
        return player;
    }

    private GameSession createSession() {
        return gameSessionRepository.save(GameSession.builder()
                .name("Local Game")
                .status(GameSessionStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .startedAt(LocalDateTime.now())
                .build());
    }

    private void assignStartingTerritory(Player player) {
        List<Territory> unclaimed = territoryRepository.findByOwnerIsNull();
        if (unclaimed.isEmpty()) {
            throw new IllegalStateException("No unclaimed territory remains to assign to a new player");
        }
        Territory starting = unclaimed.get(ThreadLocalRandom.current().nextInt(unclaimed.size()));
        starting.setOwner(player);
        starting.setClaimedAt(LocalDateTime.now());
        starting.setPopulation(1000);
        territoryRepository.save(starting);
        gameResourceService.creditTerritoryResources(player.getId(), starting);
    }
}
