package com.canarda.readiness.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "assignment_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignmentRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_member_id")
    private ServiceMember serviceMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_unit_id")
    private Unit fromUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_unit_id")
    private Unit toUnit;

    private String requestedRole;

    private String requestedBy;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private LocalDateTime requestDate;

    private LocalDateTime decisionDate;

    private String decisionNotes;
}
