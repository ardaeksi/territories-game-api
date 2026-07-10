package com.canarda.readiness.game.controller;

import com.canarda.readiness.game.dto.CreatePlayerRequest;
import com.canarda.readiness.game.dto.PlayerResponse;
import com.canarda.readiness.game.service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/game/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<PlayerResponse> create(@Valid @RequestBody CreatePlayerRequest request) {
        PlayerResponse response = PlayerResponse.from(playerService.create(request.displayName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
