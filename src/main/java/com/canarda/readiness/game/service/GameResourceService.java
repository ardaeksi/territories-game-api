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

    @Transactional
    public ResourceStockpile creditTerritoryResources(Long playerId, Territory territory) {
        return credit(playerId, territory.getResources());
    }

    @Transactional
    public ResourceStockpile credit(Long playerId, Map<ResourceType, Integer> amounts) {
        ResourceStockpile stockpile = getOrCreateStockpile(playerId);
        amounts.forEach((type, amount) -> stockpile.getAmounts().merge(type, amount, Integer::sum));
        return stockpileRepository.save(stockpile);
    }

    /** Metal/Oil/Gunpowder can be held but not spent until the matching extraction building exists. */
    @Transactional(readOnly = true)
    public boolean canSpend(Long playerId, ResourceType type) {
        return switch (type) {
            case METAL -> buildingRepository.existsByTerritoryOwnerIdAndType(playerId, BuildingType.MINE);
            case OIL -> buildingRepository.existsByTerritoryOwnerIdAndType(playerId, BuildingType.OIL_RIG);
            case GUNPOWDER -> buildingRepository.existsByTerritoryOwnerIdAndType(playerId, BuildingType.GUNPOWDER_FACTORY);
            case WOOD, STONE, FOOD -> true;
        };
    }

    /** All-or-nothing: validates every cost line before deducting any of them. */
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
