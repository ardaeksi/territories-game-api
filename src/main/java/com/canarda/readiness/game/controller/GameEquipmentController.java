package com.canarda.readiness.game.controller;

import com.canarda.readiness.game.dto.GameEquipmentResponse;
import com.canarda.readiness.game.service.GameEquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game/players/{playerId}/equipment")
@RequiredArgsConstructor
public class GameEquipmentController {

    private final GameEquipmentService gameEquipmentService;

    @GetMapping
    public GameEquipmentResponse currentEquipment(@PathVariable Long playerId) {
        return GameEquipmentResponse.of(playerId, gameEquipmentService.currentAmounts(playerId));
    }
}
