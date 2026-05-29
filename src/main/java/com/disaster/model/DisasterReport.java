package com.disaster.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "disaster_reports", indexes = {
    @Index(name = "idx_location", columnList = "latitude, longitude"),
    @Index(name = "idx_severity", columnList = "severity"),
    @Index(name = "idx_created_at", columnList = "createdAt"),
    @Index(name = "idx_disaster_type", columnList = "disasterType")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DisasterReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    private DisasterType disasterType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Severity severity;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    private String address;
    private String city;
    private String country;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ReportStatus status = ReportStatus.REPORTED;

    private String reporterName;
    private String reporterContact;

    @Builder.Default
    private Integer upvotes = 0;

    @Builder.Default
    private Boolean verified = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime resolvedAt;

    public enum DisasterType {
        EARTHQUAKE, FLOOD, FIRE, CYCLONE, LANDSLIDE, TSUNAMI, DROUGHT, EPIDEMIC, INDUSTRIAL_ACCIDENT, OTHER
    }

    public enum Severity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum ReportStatus {
        REPORTED, VERIFIED, IN_PROGRESS, RESOLVED, DISMISSED
    }
}
