package com.example.icslog.domain.model;

import java.time.Instant;
import java.util.UUID;

public record ModbusEvent(
        UUID id,
        Instant frameTime,
        String srcIp,
        String dstIp,
        Integer transactionId,
        Integer protocolId,
        Integer lengthField,
        Integer unitId,
        Integer functionCode,
        String functionName,
        Integer registerAddress,   // NEW
        Integer registerValue,     // NEW (Write Single Register'daki data)
        String dataHex,
        String rawJson
) {
    public static ModbusEvent create(
            Instant frameTime,
            String srcIp,
            String dstIp,
            Integer transactionId,
            Integer protocolId,
            Integer lengthField,
            Integer unitId,
            Integer functionCode,
            String functionName,
            Integer registerAddress,
            Integer registerValue,
            String dataHex,
            String rawJson
    ) {
        return new ModbusEvent(
                UUID.randomUUID(),
                frameTime != null ? frameTime : Instant.now(),
                srcIp,
                dstIp,
                transactionId,
                protocolId,
                lengthField,
                unitId,
                functionCode,
                functionName,
                registerAddress,
                registerValue,
                dataHex,
                rawJson
        );
    }
}
