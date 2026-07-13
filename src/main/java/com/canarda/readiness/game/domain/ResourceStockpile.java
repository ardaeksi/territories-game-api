package com.canarda.readiness.game.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * A player's accumulated resource wallet. Amounts stored here are only ever a "settled"
 * baseline as of lastSettledAt - the true current amount is derived on read by adding
 * (territory yield rates * minutes elapsed) rather than written on every tick. See
 * GameResourceService.
 */
@Entity
@Table(name = "resource_stockpiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResourceStockpile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    @ElementCollection
    @CollectionTable(name = "resource_stockpile_amounts", joinColumns = @JoinColumn(name = "stockpile_id"))
    @MapKeyColumn(name = "resource_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "amount")
    @Builder.Default
    private Map<ResourceType, Double> settledAmounts = new HashMap<>();

    private LocalDateTime lastSettledAt;
}
