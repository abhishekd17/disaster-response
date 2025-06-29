package com.disaster.responseplatform.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "reports")
public class Report {
    @Id
    private String id;
    private String type;
    private String text;
    private GeoJsonPoint location;
    private LocalDateTime timestamp;
    private boolean verified;
    private double confidence;
}