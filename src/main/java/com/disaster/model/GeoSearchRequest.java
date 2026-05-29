package com.disaster.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GeoSearchRequest {
    @NotNull @Min(-90) @Max(90)
    private Double latitude;

    @NotNull @Min(-180) @Max(180)
    private Double longitude;

    @Min(1) @Max(500)
    private Double radiusKm = 50.0;

    private DisasterReport.DisasterType disasterType;
    private DisasterReport.Severity minSeverity;
}
