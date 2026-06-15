package com.disaster.service;

import com.disaster.model.DisasterReport;
import com.disaster.model.DisasterReport.*;
import com.disaster.model.GeoSearchRequest;
import com.disaster.repository.DisasterReportRepository;
import com.disaster.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisasterReportService {

    private final DisasterReportRepository repository;
    private final GeocodingService geocodingService;
    private final WebSocketNotificationService notificationService;

    @Transactional
    public DisasterReport createReport(DisasterReport report) {
        // Reverse geocode to get address details
        Map<String, String> geoData = geocodingService.reverseGeocode(
            report.getLatitude(), report.getLongitude()
        );
        report.setAddress(geoData.get("display_name"));
        report.setCity(geoData.get("city"));
        report.setCountry(geoData.get("country"));
        report.setCreatedAt(LocalDateTime.now());

        DisasterReport saved = repository.save(report);
        log.info("New disaster report created: id={}, type={}, severity={}, location=({},{})",
            saved.getId(), saved.getDisasterType(), saved.getSeverity(),
            saved.getLatitude(), saved.getLongitude());

        // Notify all connected dashboards via WebSocket
        notificationService.broadcastNewReport(saved);
        return saved;
    }

    public List<DisasterReport> searchNearby(GeoSearchRequest request) {
        List<DisasterReport> results = repository.findWithinRadius(
            request.getLatitude(), request.getLongitude(), request.getRadiusKm()
        );

        // Filter by type and severity if specified
        if (request.getDisasterType() != null) {
            results = results.stream()
                .filter(r -> r.getDisasterType() == request.getDisasterType())
                .toList();
        }
        if (request.getMinSeverity() != null) {
            results = results.stream()
                .filter(r -> r.getSeverity().ordinal() >= request.getMinSeverity().ordinal())
                .toList();
        }
        return results;
    }

    public Page<DisasterReport> getReports(int page, int size, DisasterType type, Severity severity) {
        PageRequest pageable = PageRequest.of(page, size);
        if (type != null) return repository.findByDisasterTypeOrderByCreatedAtDesc(type, pageable);
        if (severity != null) return repository.findBySeverityOrderByCreatedAtDesc(severity, pageable);
        return repository.findAll(pageable);
    }

    public DisasterReport getById(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Report not found with id: " + id));
    }

    @Transactional
    public DisasterReport updateStatus(Long id, ReportStatus newStatus) {
        DisasterReport report = getById(id);
        report.setStatus(newStatus);
        if (newStatus == ReportStatus.RESOLVED) {
            report.setResolvedAt(LocalDateTime.now());
        }
        DisasterReport saved = repository.save(report);
        notificationService.broadcastStatusUpdate(saved);
        return saved;
    }

    @Transactional
    public DisasterReport upvoteReport(Long id) {
        DisasterReport report = getById(id);
        report.setUpvotes(report.getUpvotes() + 1);
        if (report.getUpvotes() >= 5 && !report.getVerified()) {
            report.setVerified(true);
            report.setStatus(ReportStatus.VERIFIED);
        }
        return repository.save(report);
    }

    public Map<String, Object> getDashboardStats() {
        LocalDateTime last24h = LocalDateTime.now().minusHours(24);
        LocalDateTime last7d = LocalDateTime.now().minusDays(7);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalReports", repository.count());
        stats.put("reportsLast24h", repository.countByCreatedAtAfter(last24h));
        stats.put("reportsLast7d", repository.countByCreatedAtAfter(last7d));
        stats.put("activeIncidents", repository.countByStatus(ReportStatus.REPORTED)
            + repository.countByStatus(ReportStatus.VERIFIED)
            + repository.countByStatus(ReportStatus.IN_PROGRESS));
        stats.put("resolvedIncidents", repository.countByStatus(ReportStatus.RESOLVED));
        stats.put("byType", repository.countByTypeAfter(last7d));
        stats.put("bySeverity", repository.countActiveBySeverity());
        return stats;
    }

    public List<DisasterReport> getRecentReports(int hours) {
        return repository.findRecentReports(LocalDateTime.now().minusHours(hours));
    }
}
