package com.canarda.readiness.game.service;

import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.game.domain.BuildingType;
import com.canarda.readiness.game.domain.Player;
import com.canarda.readiness.game.domain.ResourceStockpile;
import com.canarda.readiness.game.domain.ResourceType;
import com.canarda.readiness.game.domain.Territory;
import com.canarda.readiness.game.repository.BuildingRepository;
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
    private final BuildingRepository buildingRepository;

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

    /**
     * Wood, Stone, and Food are always spendable. Metal/Oil/Gunpowder are captured into
     * the wallet the same as any other resource, but can't be SPENT until the player owns
     * the matching extraction building somewhere in their empire (Mine/Oil Rig/Gunpowder
     * Factory) - a wallet-level spending gate, not a capture-time one.
     */
    @Transactional(readOnly = true)
    public boolean canSpend(Long playerId, ResourceType type) {
        return switch (type) {
            case METAL -> buildingRepository.existsByTerritoryOwnerIdAndType(playerId, BuildingType.MINE);
            case OIL -> buildingRepository.existsByTerritoryOwnerIdAndType(playerId, BuildingType.OIL_RIG);
            case GUNPOWDER -> buildingRepository.existsByTerritoryOwnerIdAndType(playerId, BuildingType.GUNPOWDER_FACTORY);
            case WOOD, STONE, FOOD -> true;
        };
    }

    /**
     * Validates spendability + sufficient balance for every line of the cost, then deducts
     * all of it. Throws IllegalStateException (mapped to 409 by the existing
     * GlobalExceptionHandler) on the first failure, before anything is deducted - either
     * the whole cost is paid or none of it is.
     */
    @Transactional
    public void spend(Long playerId, Map<ResourceType, Integer> cost) {
        ResourceStockpile stockpile = getOrCreateStockpile(playerId);

        for (Map.Entry<ResourceType, Integer> line : cost.entrySet()) {
            ResourceType type = line.getKey();
            int required = line.getValue();

            if (!canSpend(playerId, type)) {
                throw new IllegalStateException(
                        "Cannot spend " + type + " yet - the required extraction building hasn't been built");
            }
            int available = stockpile.getAmounts().getOrDefault(type, 0);
            if (available < required) {
                throw new IllegalStateException(
                        "Not enough " + type + ": have " + available + ", need " + required);
            }
        }

        cost.forEach((type, required) -> stockpile.getAmounts().merge(type, -required, Integer::sum));
        stockpileRepository.save(stockpile);
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
