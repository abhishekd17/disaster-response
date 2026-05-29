package com.disaster.controller;

import com.disaster.model.DisasterReport;
import com.disaster.model.DisasterReport.*;
import com.disaster.model.GeoSearchRequest;
import com.disaster.service.DisasterReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DisasterReportController {

    private final DisasterReportService service;

    @PostMapping
    public ResponseEntity<DisasterReport> createReport(@Valid @RequestBody DisasterReport report) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createReport(report));
    }

    @GetMapping
    public ResponseEntity<Page<DisasterReport>> getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) DisasterType type,
            @RequestParam(required = false) Severity severity) {
        return ResponseEntity.ok(service.getReports(page, size, type, severity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisasterReport> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/search/nearby")
    public ResponseEntity<List<DisasterReport>> searchNearby(@Valid @RequestBody GeoSearchRequest request) {
        return ResponseEntity.ok(service.searchNearby(request));
    }

    @GetMapping("/recent")
    public ResponseEntity<List<DisasterReport>> getRecent(@RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(service.getRecentReports(hours));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DisasterReport> updateStatus(
            @PathVariable Long id,
            @RequestParam ReportStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @PostMapping("/{id}/upvote")
    public ResponseEntity<DisasterReport> upvote(@PathVariable Long id) {
        return ResponseEntity.ok(service.upvoteReport(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(service.getDashboardStats());
    }
}
