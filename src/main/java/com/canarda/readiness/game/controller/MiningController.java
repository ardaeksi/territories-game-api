package com.canarda.readiness.game.controller;

import com.canarda.readiness.game.dto.CollectMiningJobRequest;
import com.canarda.readiness.game.dto.MiningJobResponse;
import com.canarda.readiness.game.dto.MiningPresetResponse;
import com.canarda.readiness.game.dto.StartMiningJobRequest;
import com.canarda.readiness.game.service.MiningPreset;
import com.canarda.readiness.game.service.MiningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class MiningController {

    private final MiningService miningService;

    @GetMapping("/api/game/mining/presets")
    public List<MiningPresetResponse> presets() {
        return MiningPreset.all().values().stream().map(MiningPresetResponse::from).toList();
    }

    @GetMapping("/api/game/buildings/{buildingId}/mining-job")
    public ResponseEntity<MiningJobResponse> activeJob(@PathVariable Long buildingId) {
        return miningService.activeJob(buildingId)
                .map(job -> ResponseEntity.ok(MiningJobResponse.from(job)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/api/game/buildings/{buildingId}/mining-job")
    public MiningJobResponse start(@PathVariable Long buildingId, @Valid @RequestBody StartMiningJobRequest request) {
        return MiningJobResponse.from(
                miningService.startJob(buildingId, request.playerId(), request.resourceType(), request.presetKey()));
    }

    @PostMapping("/api/game/buildings/{buildingId}/mining-job/collect")
    public MiningJobResponse collect(@PathVariable Long buildingId, @Valid @RequestBody CollectMiningJobRequest request) {
        return MiningJobResponse.from(miningService.collect(buildingId, request.playerId()));
    }
}
