package com.disaster.config;

import com.disaster.model.DisasterReport;
import com.disaster.model.DisasterReport.*;
import com.disaster.repository.DisasterReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private final DisasterReportRepository repository;
    private final Random random = new Random(42);

    private static final double[][] INDIAN_CITIES = {
        {28.6139, 77.2090},   // Delhi
        {19.0760, 72.8777},   // Mumbai
        {12.9716, 77.5946},   // Bengaluru
        {13.0827, 80.2707},   // Chennai
        {22.5726, 88.3639},   // Kolkata
        {17.3850, 78.4867},   // Hyderabad
        {23.0225, 72.5714},   // Ahmedabad
        {18.5204, 73.8567},   // Pune
        {26.9124, 75.7873},   // Jaipur
        {25.5941, 85.1376},   // Patna
    };

    private static final String[] CITY_NAMES = {
        "Delhi", "Mumbai", "Bengaluru", "Chennai", "Kolkata",
        "Hyderabad", "Ahmedabad", "Pune", "Jaipur", "Patna"
    };

    @Override
    public void run(String... args) {
        if (repository.count() > 0) return;

        log.info("Seeding database with sample disaster reports...");

        DisasterType[] types = DisasterType.values();
        Severity[] severities = Severity.values();
        String[] titles = {
            "Heavy flooding in residential area", "Building fire reported",
            "Earthquake tremors felt", "Cyclone warning issued",
            "Landslide blocks highway", "Industrial gas leak",
            "Flash flood in low-lying area", "Forest fire spreading",
            "Water contamination reported", "Road collapse after rain"
        };

        for (int i = 0; i < 500; i++) {
            int cityIdx = random.nextInt(INDIAN_CITIES.length);
            double lat = INDIAN_CITIES[cityIdx][0] + (random.nextDouble() - 0.5) * 0.2;
            double lng = INDIAN_CITIES[cityIdx][1] + (random.nextDouble() - 0.5) * 0.2;

            DisasterReport report = DisasterReport.builder()
                .title(titles[random.nextInt(titles.length)])
                .description("Urgent: Assistance required in the area. Multiple people affected.")
                .disasterType(types[random.nextInt(types.length)])
                .severity(severities[random.nextInt(severities.length)])
                .latitude(lat)
                .longitude(lng)
                .city(CITY_NAMES[cityIdx])
                .country("India")
                .address(CITY_NAMES[cityIdx] + ", India")
                .reporterName("User_" + i)
                .upvotes(random.nextInt(20))
                .verified(random.nextBoolean())
                .status(ReportStatus.values()[random.nextInt(ReportStatus.values().length)])
                .createdAt(LocalDateTime.now().minusHours(random.nextInt(168)))
                .build();

            repository.save(report);
        }

        log.info("Seeded {} disaster reports across {} cities", 500, INDIAN_CITIES.length);
    }
}
