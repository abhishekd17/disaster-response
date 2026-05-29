package com.disaster.service;

import com.disaster.model.DisasterReport;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class WebSocketNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    public void broadcastNewReport(DisasterReport report) {
        try {
            String payload = objectMapper.writeValueAsString(report);
            messagingTemplate.convertAndSend("/topic/reports", payload);
            messagingTemplate.convertAndSend("/topic/reports/" + report.getDisasterType(), payload);
            log.debug("Broadcasted new report id={} to WebSocket subscribers", report.getId());
        } catch (Exception e) {
            log.error("Failed to broadcast report: {}", e.getMessage());
        }
    }

    public void broadcastStatusUpdate(DisasterReport report) {
        try {
            String payload = objectMapper.writeValueAsString(report);
            messagingTemplate.convertAndSend("/topic/status-updates", payload);
            log.debug("Broadcasted status update for report id={}", report.getId());
        } catch (Exception e) {
            log.error("Failed to broadcast status update: {}", e.getMessage());
        }
    }
}
