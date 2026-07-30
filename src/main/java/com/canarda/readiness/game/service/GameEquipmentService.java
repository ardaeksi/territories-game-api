package com.canarda.readiness.game.service;

import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.game.domain.EquipmentType;
import com.canarda.readiness.game.domain.GameEquipment;
import com.canarda.readiness.game.domain.Player;
import com.canarda.readiness.game.repository.GameEquipmentRepository;
import com.canarda.readiness.game.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GameEquipmentService {

    private final GameEquipmentRepository equipmentRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    public Map<EquipmentType, Integer> currentAmounts(Long playerId) {
        return getOrCreateStockpile(playerId).getAmounts();
    }

    @Transactional
    public GameEquipment credit(Long playerId, Map<EquipmentType, Integer> amounts) {
        GameEquipment stockpile = getOrCreateStockpile(playerId);
        amounts.forEach((type, amount) -> stockpile.getAmounts().merge(type, amount, Integer::sum));
        return equipmentRepository.save(stockpile);
    }

    private GameEquipment getOrCreateStockpile(Long playerId) {
        return equipmentRepository.findByPlayerId(playerId).orElseGet(() -> {
            Player player = playerRepository.findById(playerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Player not found: " + playerId));
            GameEquipment fresh = GameEquipment.builder()
                    .player(player)
                    .amounts(new HashMap<>())
                    .build();
            return equipmentRepository.save(fresh);
        });
    }
}
