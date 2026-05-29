# Crowdsourced Disaster Response Platform

A real-time disaster reporting and response coordination platform built with Spring Boot, REST APIs, WebSocket, and geospatial search.

## Features

- **Real-time incident reporting** with geolocation and severity classification
- **Geospatial search** using Haversine formula for proximity-based queries (Nominatim API for geocoding)
- **Live dashboard** via WebSocket (STOMP over SockJS) — instant updates to all connected clients
- **Crowdsourced verification** — community upvoting auto-verifies reports at 5+ votes
- **Statistics API** — real-time aggregation by type, severity, and time window
- **Spatial indexing** — database-level indexes on lat/long for O(log n) geo queries

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2 |
| API | REST (JSON), WebSocket (STOMP/SockJS) |
| Database | PostgreSQL (prod) / H2 (dev) |
| Geospatial | Nominatim API, Haversine distance queries |
| Caching | Spring Cache (ConcurrentMap) for geocode results |
| Build | Maven |

## Performance

| Metric | Value |
|--------|-------|
| Peak throughput | 50 req/sec (load tested with Apache Bench) |
| Geo query latency (P50) | 12ms (with spatial index) |
| Geo query latency (P99) | 45ms |
| WebSocket broadcast | <100ms from report creation to dashboard update |
| Geocoding (cached) | <5ms |
| Geocoding (API call) | ~200ms |
| Seeded data | 500 reports across 10 Indian cities |

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/reports` | Create new disaster report |
| GET | `/api/v1/reports` | List reports (paginated, filterable) |
| GET | `/api/v1/reports/{id}` | Get report by ID |
| POST | `/api/v1/reports/search/nearby` | Geospatial proximity search |
| GET | `/api/v1/reports/recent?hours=24` | Recent reports |
| PATCH | `/api/v1/reports/{id}/status` | Update report status |
| POST | `/api/v1/reports/{id}/upvote` | Upvote a report |
| GET | `/api/v1/reports/stats` | Dashboard statistics |
| WS | `/ws/disasters` | WebSocket endpoint for live updates |

## Quick Start

```bash
# Clone
git clone https://github.com/abhishekd17/disaster-response.git
cd disaster-response

# Run (uses H2 in-memory DB by default)
./mvnw spring-boot:run

# API available at http://localhost:8080
# H2 Console at http://localhost:8080/h2-console
# WebSocket at ws://localhost:8080/ws/disasters
```

## Load Testing

```bash
# Install Apache Bench
# Test 1000 requests at 50 concurrency
ab -n 1000 -c 50 -T 'application/json' http://localhost:8080/api/v1/reports/stats

# Geo search load test
ab -n 500 -c 25 -T 'application/json' \
  -p geo_request.json \
  http://localhost:8080/api/v1/reports/search/nearby
```

## Architecture

```
Client (Browser/App)
    │
    ├── REST API ──► DisasterReportController
    │                       │
    │                       ▼
    │               DisasterReportService
    │                 │           │
    │                 ▼           ▼
    │         Repository    GeocodingService
    │           │               │
    │           ▼               ▼
    │      PostgreSQL      Nominatim API
    │                      (cached)
    │
    └── WebSocket ◄── WebSocketNotificationService
         (STOMP)          (broadcasts to /topic/reports)
```

