package com.canarda.readiness.game.service;

import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.game.domain.Player;
import com.canarda.readiness.game.domain.ResourceStockpile;
import com.canarda.readiness.game.domain.ResourceType;
import com.canarda.readiness.game.domain.Territory;
import com.canarda.readiness.game.repository.PlayerRepository;
import com.canarda.readiness.game.repository.ResourceStockpileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GameResourceService {

    private final ResourceStockpileRepository stockpileRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    public Map<ResourceType, Integer> currentAmounts(Long playerId) {
        return getOrCreateStockpile(playerId).getAmounts();
    }

    /** Adds a captured territory's resource amounts to the player's wallet, once. */
    @Transactional
    public ResourceStockpile creditTerritoryResources(Long playerId, Territory territory) {
        ResourceStockpile stockpile = getOrCreateStockpile(playerId);
        territory.getResources().forEach((type, amount) -> stockpile.getAmounts().merge(type, amount, Integer::sum));
        return stockpileRepository.save(stockpile);
    }

    private ResourceStockpile getOrCreateStockpile(Long playerId) {
        return stockpileRepository.findByPlayerId(playerId).orElseGet(() -> {
            Player player = playerRepository.findById(playerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Player not found: " + playerId));
            ResourceStockpile fresh = ResourceStockpile.builder()
                    .player(player)
                    .amounts(new HashMap<>())
                    .build();
            return stockpileRepository.save(fresh);
        });
    }
}
