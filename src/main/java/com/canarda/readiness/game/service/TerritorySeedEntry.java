package com.canarda.readiness.game.service;

import java.util.List;

public record TerritorySeedEntry(
        String countryId,
        String countryName,
        double centroidLat,
        double centroidLng,
        List<String> neighborCountryIds) {
}
