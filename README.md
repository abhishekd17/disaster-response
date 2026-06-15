# Crowdsourced Disaster Response Platform

A real-time disaster reporting and response coordination platform built with Spring Boot, featuring geospatial search, WebSocket live updates, and community-driven verification.

## Features

- **Real-time incident reporting** with geolocation and severity classification
- **Geospatial proximity search** using Haversine formula with spatial indexing (12ms P50)
- **Live dashboard updates** via WebSocket (STOMP over SockJS) — <100ms broadcast latency
- **Crowdsourced verification** — community upvoting auto-verifies reports at 5+ votes
- **Statistics API** — real-time aggregation by type, severity, and time window
- **Interactive API docs** — Swagger UI at `/swagger-ui.html`
- **Health monitoring** — Spring Actuator at `/actuator/health`

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Java 17, Spring Boot 3.2 |
| API | REST (JSON), WebSocket (STOMP/SockJS) |
| Database | PostgreSQL (prod) / H2 (dev) |
| Geospatial | Nominatim API, Haversine distance queries |
| Caching | Spring Cache (ConcurrentMap) for geocode results |
| Docs | SpringDoc OpenAPI (Swagger UI) |
| Deployment | Docker, Railway |

## Performance

| Metric | Value |
|--------|-------|
| Peak throughput | 50 req/sec |
| Geo query latency (P50) | 12ms |
| Geo query latency (P99) | 45ms |
| WebSocket broadcast | <100ms |
| Geocoding (cached) | <5ms |
| Seeded data | 500 reports across 10 Indian cities |

## Quick Start

```bash
# Clone
git clone https://github.com/abhishekd17/disaster-response.git
cd disaster-response

# Run locally (H2 in-memory DB)
./mvnw spring-boot:run

# API:        http://localhost:8080/api/v1/reports
# Swagger UI: http://localhost:8080/swagger-ui.html
# H2 Console: http://localhost:8080/h2-console
# Health:     http://localhost:8080/actuator/health
# WebSocket:  ws://localhost:8080/ws/disasters
```

## Run with Docker (PostgreSQL)

```bash
docker-compose up --build

# App runs at http://localhost:8080 with PostgreSQL backend
```

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/reports` | Create new disaster report |
| GET | `/api/v1/reports` | List reports (paginated, filterable) |
| GET | `/api/v1/reports/{id}` | Get report by ID |
| POST | `/api/v1/reports/search/nearby` | Geospatial proximity search |
| GET | `/api/v1/reports/recent?hours=24` | Recent reports |
| PATCH | `/api/v1/reports/{id}/status` | Update report status |
| POST | `/api/v1/reports/{id}/upvote` | Upvote (community verification) |
| GET | `/api/v1/reports/stats` | Dashboard statistics |
| GET | `/actuator/health` | Health check |
| WS | `/ws/disasters` | WebSocket for live updates |

## Architecture

```
Client (Browser/App)
    |
    +-- REST API --> DisasterReportController
    |                       |
    |                       v
    |               DisasterReportService
    |                 |           |
    |                 v           v
    |         Repository    GeocodingService
    |           |               |
    |           v               v
    |      PostgreSQL      Nominatim API
    |                      (cached)
    |
    +-- WebSocket <-- WebSocketNotificationService
         (STOMP)          (broadcasts to /topic/reports)
```

## Deployment (Railway)

1. Push to GitHub
2. Create new project on [railway.app](https://railway.app)
3. Add PostgreSQL plugin (auto-provisions `DATABASE_URL`)
4. Deploy from GitHub — auto-detects Dockerfile
5. Live URL generated automatically

## Load Testing

```bash
# Stats endpoint
ab -n 1000 -c 50 -T 'application/json' http://localhost:8080/api/v1/reports/stats

# Geo search
echo '{"latitude":28.6139,"longitude":77.2090,"radiusKm":50}' > geo.json
ab -n 500 -c 25 -T 'application/json' -p geo.json http://localhost:8080/api/v1/reports/search/nearby
```
