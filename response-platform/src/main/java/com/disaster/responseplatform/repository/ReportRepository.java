package com.disaster.responseplatform.repository;

import com.disaster.responseplatform.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportRepository extends MongoRepository<Report, String> {
}