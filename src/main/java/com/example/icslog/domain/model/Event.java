package com.example.icslog.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Event(
        UUID id,
        String deviceId,
        Instant timestamp,
        Severity severity,
        String message,
        String sourceIp
) {
    public static Event create(String deviceId, Severity severity, String message, String sourceIp) {
        return new Event(
                UUID.randomUUID(),
                deviceId,
                Instant.now(),
                severity,
                message,
                sourceIp
        );
    }
}
