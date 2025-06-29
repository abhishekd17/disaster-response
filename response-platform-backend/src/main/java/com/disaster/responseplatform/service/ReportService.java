package com.disaster.responseplatform.service;

import com.disaster.responseplatform.dto.ReportDTO;
import com.disaster.responseplatform.model.Report;
import com.disaster.responseplatform.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReportService {
    @Autowired
    private ReportRepository reportRepository;

    public Report saveReport(ReportDTO reportDTO) {
        Report report = new Report();
        report.setType(reportDTO.getType());
        report.setText(reportDTO.getText());
        report.setLocation(new GeoJsonPoint(reportDTO.getLongitude(), reportDTO.getLatitude()));
        report.setTimestamp(LocalDateTime.now());
        report.setVerified(false);
        report.setConfidence(0.0);
        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Optional<Report> getReportById(String id) {
        return reportRepository.findById(id);
    }

    public Optional<Report> updateReport(String id, ReportDTO reportDTO) {
        return reportRepository.findById(id).map(report -> {
            report.setType(reportDTO.getType());
            report.setText(reportDTO.getText());
            report.setLocation(new GeoJsonPoint(reportDTO.getLongitude(), reportDTO.getLatitude()));
            return reportRepository.save(report);
        });
    }

    public boolean deleteReport(String id) {
        if (reportRepository.existsById(id)) {
            reportRepository.deleteById(id);
            return true;
        }
        return false;
    }
}