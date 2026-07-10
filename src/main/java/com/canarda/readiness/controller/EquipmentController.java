package com.canarda.readiness.controller;

import com.canarda.readiness.domain.Equipment;
import com.canarda.readiness.dto.CreateEquipmentRequest;
import com.canarda.readiness.dto.EquipmentResponse;
import com.canarda.readiness.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping
    public List<EquipmentResponse> findAll(@RequestParam(required = false) Long unitId) {
        List<Equipment> equipment = unitId != null ? equipmentService.findByUnit(unitId) : equipmentService.findAll();
        return equipment.stream().map(EquipmentResponse::from).toList();
    }

    @GetMapping("/{id}")
    public EquipmentResponse findById(@PathVariable Long id) {
        return EquipmentResponse.from(equipmentService.findById(id));
    }

    @PostMapping
    public ResponseEntity<EquipmentResponse> create(@Valid @RequestBody CreateEquipmentRequest request) {
        EquipmentResponse response = EquipmentResponse.from(equipmentService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
