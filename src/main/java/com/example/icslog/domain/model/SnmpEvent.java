package com.example.icslog.domain.model;

import java.time.Instant;
import java.util.UUID;

public record SnmpEvent(
        UUID id,
        Instant frameTime,
        String srcIp,
        String dstIp,
        String community,
        String version,
        String dataType,
        String oid,
        String value,
        String rawJson
) {
    public static SnmpEvent create(
            Instant frameTime,
            String srcIp,
            String dstIp,
            String community,
            String version,
            String dataType,
            String oid,
            String value,
            String rawJson
    ) {
        return new SnmpEvent(
                UUID.randomUUID(),
                frameTime != null ? frameTime : Instant.now(),
                srcIp,
                dstIp,
                community,
                version,
                dataType,
                oid,
                value,
                rawJson
        );
    }
}
