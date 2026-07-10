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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "territories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Territory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String countryId;

    private String countryName;

    private double centroidLat;

    private double centroidLng;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Player owner;

    private LocalDateTime claimedAt;

    private int population;

    @ElementCollection
    @CollectionTable(name = "territory_resources", joinColumns = @JoinColumn(name = "territory_id"))
    @MapKeyColumn(name = "resource_type")
    @MapKeyEnumerated(EnumType.STRING)
    @Column(name = "amount")
    @Builder.Default
    private Map<ResourceType, Integer> resources = new HashMap<>();
}
