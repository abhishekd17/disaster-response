package com.disaster.repository;

import com.disaster.model.DisasterReport;
import com.disaster.model.DisasterReport.DisasterType;
import com.disaster.model.DisasterReport.Severity;
import com.disaster.model.DisasterReport.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DisasterReportRepository extends JpaRepository<DisasterReport, Long> {

    // Geospatial query using Haversine formula for distance calculation
    @Query(value = """
        SELECT * FROM disaster_reports
        WHERE (6371 * acos(cos(radians(:lat)) * cos(radians(latitude))
        * cos(radians(longitude) - radians(:lng))
        + sin(radians(:lat)) * sin(radians(latitude)))) < :radius
        ORDER BY created_at DESC
        """, nativeQuery = true)
    List<DisasterReport> findWithinRadius(
        @Param("lat") double latitude,
        @Param("lng") double longitude,
        @Param("radius") double radiusKm
    );

    Page<DisasterReport> findByDisasterTypeOrderByCreatedAtDesc(DisasterType type, Pageable pageable);

    Page<DisasterReport> findBySeverityOrderByCreatedAtDesc(Severity severity, Pageable pageable);

    List<DisasterReport> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    @Query("SELECT r FROM DisasterReport r WHERE r.createdAt >= :since ORDER BY r.createdAt DESC")
    List<DisasterReport> findRecentReports(@Param("since") LocalDateTime since);

    @Query("SELECT r.disasterType, COUNT(r) FROM DisasterReport r WHERE r.createdAt >= :since GROUP BY r.disasterType")
    List<Object[]> countByTypeAfter(@Param("since") LocalDateTime since);

    @Query("SELECT r.severity, COUNT(r) FROM DisasterReport r WHERE r.status != 'RESOLVED' GROUP BY r.severity")
    List<Object[]> countActiveBySeverity();

    long countByCreatedAtAfter(LocalDateTime since);

    long countByStatus(ReportStatus status);
}
