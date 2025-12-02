package com.example.icslog.adapters.out.db.entity;

import java.time.Instant;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "modbus_events")
@Data
@NoArgsConstructor
public class ModbusEventEntity {

    @Id
    private UUID id;

    private Instant frameTime;
    private String srcIp;
    private String dstIp;

    private Integer transactionId;
    private Integer protocolId;
    private Integer lengthField;
    private Integer unitId;
    private Integer functionCode;
    private String functionName;

    private Integer registerAddress;  
    private Integer registerValue;    

    @Column(columnDefinition = "TEXT")
    private String dataHex;

    @Column(columnDefinition = "TEXT")
    private String rawJson;
}
