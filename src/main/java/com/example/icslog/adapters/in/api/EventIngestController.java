package com.example.icslog.adapters.in.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.icslog.application.port.in.IngestEventPort;
import com.example.icslog.domain.model.Event;
import com.example.icslog.domain.model.Severity;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventIngestController {

    private final IngestEventPort ingestEventPort;

    public record IngestEventRequest(String deviceId, String severity, String message, String sourceIp) {}

    @PostMapping
    public ResponseEntity<Void> ingestEvent(@RequestBody IngestEventRequest request) {
        try {
            Severity sev = Severity.valueOf(request.severity().toUpperCase());
            Event event = Event.create(request.deviceId(), sev, request.message(), request.sourceIp());
            ingestEventPort.ingestEvent(event);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
/*
SELECT BIN_TO_UUID(id) AS id,
       frame_time,
       src_ip,
       dst_ip,
       community,
       version,
       oid,
       value
FROM snmp_events;
 */