package com.canarda.readiness.game.service;

import com.canarda.readiness.exception.ResourceNotFoundException;
import com.canarda.readiness.game.domain.EquipmentType;
import com.canarda.readiness.game.domain.GameEquipment;
import com.canarda.readiness.game.domain.Player;
import com.canarda.readiness.game.repository.GameEquipmentRepository;
import com.canarda.readiness.game.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameEquipmentServiceTest {

    @Mock
    private GameEquipmentRepository equipmentRepository;
    @Mock
    private PlayerRepository playerRepository;

    private GameEquipmentService gameEquipmentService;

    @BeforeEach
    void setUp() {
        gameEquipmentService = new GameEquipmentService(equipmentRepository, playerRepository);
    }

    @Test
    void currentAmounts_returnsExistingStockpileAmounts() {
        GameEquipment stockpile = GameEquipment.builder()
                .player(Player.builder().id(6L).build())
                .amounts(new HashMap<>(Map.of(EquipmentType.SHIP, 2)))
                .build();
        when(equipmentRepository.findByPlayerId(6L)).thenReturn(Optional.of(stockpile));

        assertThat(gameEquipmentService.currentAmounts(6L)).containsEntry(EquipmentType.SHIP, 2);
    }

    @Test
    void currentAmounts_lazilyCreatesStockpileForFirstTimePlayer() {
        Player player = Player.builder().id(6L).build();
        when(equipmentRepository.findByPlayerId(6L)).thenReturn(Optional.empty());
        when(playerRepository.findById(6L)).thenReturn(Optional.of(player));
        when(equipmentRepository.save(any(GameEquipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Map<EquipmentType, Integer> amounts = gameEquipmentService.currentAmounts(6L);

        assertThat(amounts).isEmpty();
    }

    @Test
    void currentAmounts_rejectsUnknownPlayer() {
        when(equipmentRepository.findByPlayerId(6L)).thenReturn(Optional.empty());
        when(playerRepository.findById(6L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gameEquipmentService.currentAmounts(6L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void credit_mergesOntoExistingAmounts() {
        GameEquipment stockpile = GameEquipment.builder()
                .player(Player.builder().id(6L).build())
                .amounts(new HashMap<>(Map.of(EquipmentType.GUN, 3)))
                .build();
        when(equipmentRepository.findByPlayerId(6L)).thenReturn(Optional.of(stockpile));
        when(equipmentRepository.save(any(GameEquipment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GameEquipment result = gameEquipmentService.credit(6L, Map.of(EquipmentType.GUN, 2, EquipmentType.TANK, 1));

        assertThat(result.getAmounts())
                .containsEntry(EquipmentType.GUN, 5)
                .containsEntry(EquipmentType.TANK, 1);
    }
}
