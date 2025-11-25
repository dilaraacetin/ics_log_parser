package com.example.icslog.domain.model;

import java.time.Instant;
import java.util.UUID;

public record SyslogEvent(
        UUID id,
        String messageSourceAddress,
        Instant eventReceivedTime,
        String sourceModuleName,
        String sourceModuleType,
        String hostname,
        Integer syslogFacilityValue,
        String syslogFacility,
        Integer syslogSeverityValue,
        String syslogSeverity,
        Integer severityValue,
        String severity,
        Instant eventTime,
        String message
) {
    public static SyslogEvent create(
            String messageSourceAddress,
            Instant eventReceivedTime,
            String sourceModuleName,
            String sourceModuleType,
            String hostname,
            Integer syslogFacilityValue,
            String syslogFacility,
            Integer syslogSeverityValue,
            String syslogSeverity,
            Integer severityValue,
            String severity,
            Instant eventTime,
            String message
    ) {
        return new SyslogEvent(
                UUID.randomUUID(),
                messageSourceAddress,
                eventReceivedTime != null ? eventReceivedTime : Instant.now(),
                sourceModuleName,
                sourceModuleType,
                hostname,
                syslogFacilityValue,
                syslogFacility,
                syslogSeverityValue,
                syslogSeverity,
                severityValue,
                severity,
                eventTime,
                message
        );
    }
}
