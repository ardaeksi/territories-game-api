package com.canarda.readiness.game.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class TerritoryAdjacencyService {

    private final List<TerritorySeedEntry> territories;
    private final Map<String, Set<String>> adjacency;

    public TerritoryAdjacencyService() {
        this.territories = loadTerritories();
        this.adjacency = buildAdjacencyMap(territories);
    }

    private List<TerritorySeedEntry> loadTerritories() {
        try (InputStream input = new ClassPathResource("game/territories.json").getInputStream()) {
            return new ObjectMapper().readValue(input, new TypeReference<List<TerritorySeedEntry>>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load game/territories.json", e);
        }
    }

    private Map<String, Set<String>> buildAdjacencyMap(List<TerritorySeedEntry> entries) {
        Map<String, Set<String>> graph = new HashMap<>();
        for (TerritorySeedEntry entry : entries) {
            graph.put(entry.countryId(), new HashSet<>(entry.neighborCountryIds()));
        }
        return graph;
    }

    public List<TerritorySeedEntry> getAllTerritories() {
        return territories;
    }

    public boolean areAdjacent(String countryIdA, String countryIdB) {
        return adjacency.getOrDefault(countryIdA, Set.of()).contains(countryIdB);
    }
}
