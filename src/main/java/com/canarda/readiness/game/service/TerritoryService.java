package com.canarda.readiness.game.service;

import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.game.domain.Player;
import com.canarda.readiness.game.domain.Territory;
import com.canarda.readiness.game.repository.PlayerRepository;
import com.canarda.readiness.game.repository.TerritoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TerritoryService {

    private final TerritoryRepository territoryRepository;
    private final PlayerRepository playerRepository;
    private final TerritoryAdjacencyService adjacencyService;

    public List<Territory> findAll() {
        return territoryRepository.findAll();
    }

    public List<Territory> findByOwner(Long ownerId) {
        return territoryRepository.findByOwnerId(ownerId);
    }

    public Territory claim(Long territoryId, Long playerId) {
        Territory territory = territoryRepository.findById(territoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Territory not found: " + territoryId));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException("Player not found: " + playerId));

        if (territory.getOwner() != null) {
            throw new IllegalStateException("Territory " + territory.getCountryName() + " is already claimed");
        }

        List<Territory> ownedTerritories = territoryRepository.findByOwnerId(playerId);
        boolean adjacentToOwnedTerritory = ownedTerritories.stream()
                .anyMatch(owned -> adjacencyService.areAdjacent(territory.getCountryId(), owned.getCountryId()));

        if (!adjacentToOwnedTerritory) {
            throw new IllegalStateException(
                    "Territory " + territory.getCountryName() + " is not adjacent to any territory you own");
        }

        territory.setOwner(player);
        territory.setClaimedAt(LocalDateTime.now());
        territory.setPopulation(200);
        return territoryRepository.save(territory);
    }
}
