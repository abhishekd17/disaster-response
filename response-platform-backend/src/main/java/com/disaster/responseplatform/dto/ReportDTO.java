package com.disaster.responseplatform.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

@Data
public class ReportDTO {
    @NotNull(message = "Type cannot be null")
    private String type;

    @NotNull(message = "Text cannot be null")
    private String text;

    @NotNull(message = "Latitude cannot be null")
    private Double latitude;

    @NotNull(message = "Longitude cannot be null")
    private Double longitude;
}