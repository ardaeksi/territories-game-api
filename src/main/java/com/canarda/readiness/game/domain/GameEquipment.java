package com.canarda.readiness.game.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/** A player's crafted-equipment stockpile - credited by CraftingService on job collection. */
@Entity
@Table(name = "game_equipment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameEquipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ElementCollection
    @CollectionTable(name = "game_equipment_amounts", joinColumns = @JoinColumn(name = "equipment_id"))
    @MapKeyColumn(name = "equipment_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "amount")
    @Builder.Default
    private Map<EquipmentType, Integer> amounts = new HashMap<>();
}
