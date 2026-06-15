package com.disaster.controller;

import com.disaster.model.DisasterReport;
import com.disaster.model.DisasterReport.*;
import com.disaster.model.GeoSearchRequest;
import com.disaster.service.DisasterReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Disaster Reports", description = "CRUD and search operations for disaster reports")
public class DisasterReportController {

    private final DisasterReportService service;

    @PostMapping
    @Operation(summary = "Create a new disaster report", description = "Submit a new disaster incident with location, type, and severity")
    public ResponseEntity<DisasterReport> createReport(@Valid @RequestBody DisasterReport report) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createReport(report));
    }

    @GetMapping
    @Operation(summary = "List all reports", description = "Paginated list of reports, filterable by type and severity")
    public ResponseEntity<Page<DisasterReport>> getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) DisasterType type,
            @RequestParam(required = false) Severity severity) {
        return ResponseEntity.ok(service.getReports(page, size, type, severity));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get report by ID")
    public ResponseEntity<DisasterReport> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping("/search/nearby")
    @Operation(summary = "Geospatial proximity search", description = "Find disaster reports within a given radius using Haversine distance calculation")
    public ResponseEntity<List<DisasterReport>> searchNearby(@Valid @RequestBody GeoSearchRequest request) {
        return ResponseEntity.ok(service.searchNearby(request));
    }

    @GetMapping("/recent")
    @Operation(summary = "Get recent reports", description = "Returns reports from the last N hours")
    public ResponseEntity<List<DisasterReport>> getRecent(@RequestParam(defaultValue = "24") int hours) {
        return ResponseEntity.ok(service.getRecentReports(hours));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update report status", description = "Change status to VERIFIED, IN_PROGRESS, RESOLVED, or DISMISSED")
    public ResponseEntity<DisasterReport> updateStatus(
            @PathVariable Long id,
            @RequestParam ReportStatus status) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }

    @PostMapping("/{id}/upvote")
    @Operation(summary = "Upvote a report", description = "Community verification — reports with 5+ upvotes are auto-verified")
    public ResponseEntity<DisasterReport> upvote(@PathVariable Long id) {
        return ResponseEntity.ok(service.upvoteReport(id));
    }

    @GetMapping("/stats")
    @Operation(summary = "Dashboard statistics", description = "Aggregated stats by type, severity, and time window")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(service.getDashboardStats());
    }
}
