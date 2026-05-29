package com.disaster.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@Slf4j
public class GeocodingService {

    private final WebClient nominatimClient;

    public GeocodingService() {
        this.nominatimClient = WebClient.builder()
            .baseUrl("https://nominatim.openstreetmap.org")
            .defaultHeader("User-Agent", "DisasterResponsePlatform/1.0")
            .build();
    }

    @Cacheable(value = "geocode", key = "#latitude + ',' + #longitude")
    public Map<String, String> reverseGeocode(double latitude, double longitude) {
        try {
            Map response = nominatimClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/reverse")
                    .queryParam("format", "json")
                    .queryParam("lat", latitude)
                    .queryParam("lon", longitude)
                    .queryParam("zoom", 14)
                    .build())
                .retrieve()
                .bodyToMono(Map.class)
                .block();

            if (response != null && response.containsKey("address")) {
                Map<String, String> address = (Map<String, String>) response.get("address");
                return Map.of(
                    "display_name", (String) response.getOrDefault("display_name", ""),
                    "city", address.getOrDefault("city", address.getOrDefault("town", "")),
                    "country", address.getOrDefault("country", "")
                );
            }
        } catch (Exception e) {
            log.warn("Geocoding failed for ({}, {}): {}", latitude, longitude, e.getMessage());
        }
        return Map.of("display_name", "", "city", "", "country", "");
    }

    @Cacheable(value = "forward_geocode", key = "#query")
    public double[] forwardGeocode(String query) {
        try {
            Object[] response = nominatimClient.get()
                .uri(uriBuilder -> uriBuilder
                    .path("/search")
                    .queryParam("format", "json")
                    .queryParam("q", query)
                    .queryParam("limit", 1)
                    .build())
                .retrieve()
                .bodyToMono(Object[].class)
                .block();

            if (response != null && response.length > 0) {
                Map<String, Object> result = (Map<String, Object>) response[0];
                double lat = Double.parseDouble((String) result.get("lat"));
                double lon = Double.parseDouble((String) result.get("lon"));
                return new double[]{lat, lon};
            }
        } catch (Exception e) {
            log.warn("Forward geocoding failed for '{}': {}", query, e.getMessage());
        }
        return null;
    }
}
