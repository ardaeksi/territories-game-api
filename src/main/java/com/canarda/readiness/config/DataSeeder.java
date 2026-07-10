package com.canarda.readiness.config;

import com.canarda.readiness.domain.AssignmentRequest;
import com.canarda.readiness.domain.Branch;
import com.canarda.readiness.domain.Equipment;
import com.canarda.readiness.domain.EquipmentStatus;
import com.canarda.readiness.domain.EquipmentType;
import com.canarda.readiness.domain.ReadinessStatus;
import com.canarda.readiness.domain.Rank;
import com.canarda.readiness.domain.RequestStatus;
import com.canarda.readiness.domain.ServiceMember;
import com.canarda.readiness.domain.Unit;
import com.canarda.readiness.repository.AssignmentRequestRepository;
import com.canarda.readiness.repository.EquipmentRepository;
import com.canarda.readiness.repository.ServiceMemberRepository;
import com.canarda.readiness.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UnitRepository unitRepository;
    private final ServiceMemberRepository serviceMemberRepository;
    private final AssignmentRequestRepository assignmentRequestRepository;
    private final EquipmentRepository equipmentRepository;

    @Override
    public void run(String... args) {
        if (unitRepository.count() > 0) {
            return;
        }

        Unit logisticsUnit = unitRepository.save(Unit.builder()
                .name("1st Logistics Unit")
                .branch(Branch.ARMY)
                .location("Camp Ironwood (fictional)")
                .latitude(40.0)
                .longitude(-105.5)
                .build());

        Unit signalUnit = unitRepository.save(Unit.builder()
                .name("3rd Signal Unit")
                .branch(Branch.ARMY)
                .location("Fort Meridian (fictional)")
                .latitude(48.5)
                .longitude(2.0)
                .build());

        ServiceMember alvarez = serviceMemberRepository.save(ServiceMember.builder()
                .serviceNumber("A100234")
                .firstName("Luis")
                .lastName("Alvarez")
                .rank(Rank.CORPORAL)
                .readinessStatus(ReadinessStatus.READY)
                .unit(logisticsUnit)
                .build());

        serviceMemberRepository.save(ServiceMember.builder()
                .serviceNumber("R200456")
                .firstName("Elena")
                .lastName("Reyes")
                .rank(Rank.CAPTAIN)
                .readinessStatus(ReadinessStatus.READY)
                .unit(signalUnit)
                .build());

        serviceMemberRepository.save(ServiceMember.builder()
                .serviceNumber("K300789")
                .firstName("Daniel")
                .lastName("Kim")
                .rank(Rank.SERGEANT)
                .readinessStatus(ReadinessStatus.LIMITED)
                .unit(logisticsUnit)
                .build());

        assignmentRequestRepository.save(AssignmentRequest.builder()
                .serviceMember(alvarez)
                .fromUnit(logisticsUnit)
                .toUnit(signalUnit)
                .requestedRole("Radio Operator")
                .requestedBy("Capt. Reyes")
                .status(RequestStatus.PENDING)
                .requestDate(LocalDateTime.now())
                .build());

        equipmentRepository.save(Equipment.builder()
                .name("M4 Carbine")
                .type(EquipmentType.GUN)
                .quantity(40)
                .status(EquipmentStatus.OPERATIONAL)
                .unit(logisticsUnit)
                .build());

        equipmentRepository.save(Equipment.builder()
                .name("Humvee")
                .type(EquipmentType.CAR)
                .quantity(5)
                .status(EquipmentStatus.OPERATIONAL)
                .unit(logisticsUnit)
                .build());

        equipmentRepository.save(Equipment.builder()
                .name("Cargo Truck")
                .type(EquipmentType.TRUCK)
                .quantity(3)
                .status(EquipmentStatus.MAINTENANCE)
                .unit(logisticsUnit)
                .build());

        equipmentRepository.save(Equipment.builder()
                .name("M4 Carbine")
                .type(EquipmentType.GUN)
                .quantity(25)
                .status(EquipmentStatus.OPERATIONAL)
                .unit(signalUnit)
                .build());

        equipmentRepository.save(Equipment.builder()
                .name("M1 Abrams")
                .type(EquipmentType.TANK)
                .quantity(2)
                .status(EquipmentStatus.OPERATIONAL)
                .unit(signalUnit)
                .build());

        equipmentRepository.save(Equipment.builder()
                .name("UH-60 Black Hawk")
                .type(EquipmentType.HELICOPTER)
                .quantity(1)
                .status(EquipmentStatus.MAINTENANCE)
                .unit(signalUnit)
                .build());

        equipmentRepository.save(Equipment.builder()
                .name("Staff Sedan")
                .type(EquipmentType.CIVILIAN_CAR)
                .quantity(2)
                .status(EquipmentStatus.OPERATIONAL)
                .unit(signalUnit)
                .build());
    }
}
