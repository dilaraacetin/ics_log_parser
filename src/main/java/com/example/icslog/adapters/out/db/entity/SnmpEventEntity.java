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
@Table(name = "snmp_events")
@Data
@NoArgsConstructor
public class SnmpEventEntity {

    @Id
    private UUID id;

    private Instant frameTime;
    private String srcIp;
    private String dstIp;
    private String community;
    private String version;
    private String dataType;
    private String oid;

    @Column(columnDefinition = "TEXT")
    private String value;

    @Column(columnDefinition = "TEXT")
    private String rawJson;
}
