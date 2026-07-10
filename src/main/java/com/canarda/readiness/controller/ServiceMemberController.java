package com.canarda.readiness.controller;

import com.canarda.readiness.domain.ReadinessStatus;
import com.canarda.readiness.dto.CreateServiceMemberRequest;
import com.canarda.readiness.dto.ServiceMemberResponse;
import com.canarda.readiness.service.ServiceMemberService;
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
@RequestMapping("/api/service-members")
@RequiredArgsConstructor
public class ServiceMemberController {

    private final ServiceMemberService serviceMemberService;

    @GetMapping
    public List<ServiceMemberResponse> findAll(
            @RequestParam(required = false) Long unitId,
            @RequestParam(required = false) ReadinessStatus status) {

        if (unitId != null) {
            return serviceMemberService.findByUnit(unitId).stream().map(ServiceMemberResponse::from).toList();
        }
        if (status != null) {
            return serviceMemberService.findByReadinessStatus(status).stream().map(ServiceMemberResponse::from).toList();
        }
        return serviceMemberService.findAll().stream().map(ServiceMemberResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ServiceMemberResponse findById(@PathVariable Long id) {
        return ServiceMemberResponse.from(serviceMemberService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ServiceMemberResponse> create(@Valid @RequestBody CreateServiceMemberRequest request) {
        ServiceMemberResponse response = ServiceMemberResponse.from(serviceMemberService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
