package com.canarda.readiness.controller;

import com.canarda.readiness.dto.CreateUnitRequest;
import com.canarda.readiness.dto.UnitResponse;
import com.canarda.readiness.service.UnitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/units")
@RequiredArgsConstructor
public class UnitController {

    private final UnitService unitService;

    @GetMapping
    public List<UnitResponse> findAll() {
        return unitService.findAll().stream().map(UnitResponse::from).toList();
    }

    @GetMapping("/{id}")
    public UnitResponse findById(@PathVariable Long id) {
        return UnitResponse.from(unitService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UnitResponse> create(@Valid @RequestBody CreateUnitRequest request) {
        UnitResponse response = UnitResponse.from(unitService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
