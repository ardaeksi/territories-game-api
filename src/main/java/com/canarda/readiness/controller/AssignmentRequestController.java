package com.canarda.readiness.controller;

import com.canarda.readiness.domain.RequestStatus;
import com.canarda.readiness.dto.AssignmentRequestResponse;
import com.canarda.readiness.dto.CreateAssignmentRequestRequest;
import com.canarda.readiness.dto.DecisionRequest;
import com.canarda.readiness.service.AssignmentRequestService;
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
@RequestMapping("/api/assignment-requests")
@RequiredArgsConstructor
public class AssignmentRequestController {

    private final AssignmentRequestService assignmentRequestService;

    @GetMapping
    public List<AssignmentRequestResponse> findAll(@RequestParam(required = false) RequestStatus status) {
        var requests = status != null
                ? assignmentRequestService.findByStatus(status)
                : assignmentRequestService.findAll();
        return requests.stream().map(AssignmentRequestResponse::from).toList();
    }

    @GetMapping("/{id}")
    public AssignmentRequestResponse findById(@PathVariable Long id) {
        return AssignmentRequestResponse.from(assignmentRequestService.findById(id));
    }

    @PostMapping
    public ResponseEntity<AssignmentRequestResponse> create(@Valid @RequestBody CreateAssignmentRequestRequest request) {
        AssignmentRequestResponse response = AssignmentRequestResponse.from(assignmentRequestService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/approve")
    public AssignmentRequestResponse approve(@PathVariable Long id, @RequestBody(required = false) DecisionRequest request) {
        String notes = request != null ? request.notes() : null;
        return AssignmentRequestResponse.from(assignmentRequestService.approve(id, notes));
    }

    @PostMapping("/{id}/deny")
    public AssignmentRequestResponse deny(@PathVariable Long id, @RequestBody(required = false) DecisionRequest request) {
        String notes = request != null ? request.notes() : null;
        return AssignmentRequestResponse.from(assignmentRequestService.deny(id, notes));
    }
}
