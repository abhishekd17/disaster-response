package com.disaster.responseplatform.controller;

import com.disaster.responseplatform.model.Report;
import com.disaster.responseplatform.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import java.time.LocalDateTime;

@RestController
public class TestController {
    @Autowired
    private ReportRepository reportRepository;

    @GetMapping("/test")
    public String testMongo() {
        Report report = new Report();
        report.setType("flood");
        report.setText("Flood near River Street");
        report.setLocation(new GeoJsonPoint(77.6, 28.6));
        report.setTimestamp(LocalDateTime.now());
        report.setVerified(false);
        report.setConfidence(0.0);
        reportRepository.save(report);
        return "Report saved!";
    }
}