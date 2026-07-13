package com.canarda.readiness.game.service;

import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.game.domain.Player;
import com.canarda.readiness.game.domain.ResourceStockpile;
import com.canarda.readiness.game.domain.ResourceType;
import com.canarda.readiness.game.domain.Territory;
import com.canarda.readiness.game.repository.PlayerRepository;
import com.canarda.readiness.game.repository.ResourceStockpileRepository;
import com.canarda.readiness.game.repository.TerritoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Resource amounts are never written just to be displayed. A ResourceStockpile stores a
 * "settled" baseline as of lastSettledAt; the true current amount is always derived on
 * read as settled + (sum of owned territories' yield-per-minute * minutes elapsed). A
 * write only happens when something mutates the stockpile (settle()) - an idle player
 * costs zero DB writes no matter how long they've been away.
 */
@Service
@RequiredArgsConstructor
public class GameResourceService {

    private final ResourceStockpileRepository stockpileRepository;
    private final PlayerRepository playerRepository;
    private final TerritoryRepository territoryRepository;

    @Transactional
    public Map<ResourceType, Double> currentAmounts(Long playerId) {
        return deriveAmounts(getOrCreateStockpile(playerId));
    }

    @Transactional
    public ResourceStockpile settle(Long playerId) {
        ResourceStockpile stockpile = getOrCreateStockpile(playerId);
        stockpile.setSettledAmounts(deriveAmounts(stockpile));
        stockpile.setLastSettledAt(LocalDateTime.now());
        return stockpileRepository.save(stockpile);
    }

    private Map<ResourceType, Double> deriveAmounts(ResourceStockpile stockpile) {
        Map<ResourceType, Double> yieldPerMinute = totalYieldPerMinute(stockpile.getPlayer().getId());
        double minutesElapsed = Duration.between(stockpile.getLastSettledAt(), LocalDateTime.now()).toMillis() / 60000.0;

        Map<ResourceType, Double> current = new EnumMap<>(ResourceType.class);
        for (ResourceType type : ResourceType.values()) {
            double settled = stockpile.getSettledAmounts().getOrDefault(type, 0.0);
            double rate = yieldPerMinute.getOrDefault(type, 0.0);
            current.put(type, settled + rate * minutesElapsed);
        }
        return current;
    }

    private Map<ResourceType, Double> totalYieldPerMinute(Long playerId) {
        Map<ResourceType, Double> total = new EnumMap<>(ResourceType.class);
        for (Territory territory : territoryRepository.findByOwnerId(playerId)) {
            territory.getResources().forEach((type, amount) -> total.merge(type, amount.doubleValue(), Double::sum));
        }
        return total;
    }

    private ResourceStockpile getOrCreateStockpile(Long playerId) {
        return stockpileRepository.findByPlayerId(playerId).orElseGet(() -> {
            Player player = playerRepository.findById(playerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Player not found: " + playerId));
            ResourceStockpile fresh = ResourceStockpile.builder()
                    .player(player)
                    .settledAmounts(new HashMap<>())
                    .lastSettledAt(LocalDateTime.now())
                    .build();
            return stockpileRepository.save(fresh);
        });
    }
}
