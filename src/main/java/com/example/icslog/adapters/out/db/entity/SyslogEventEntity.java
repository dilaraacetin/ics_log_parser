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
@Table(name = "syslog_events")
@Data
@NoArgsConstructor
public class SyslogEventEntity {

    @Id
    private UUID id;

    private String messageSourceAddress;
    private Instant eventReceivedTime;
    private String sourceModuleName;
    private String sourceModuleType;
    private String hostname;
    private Integer syslogFacilityValue;
    private String syslogFacility;
    private Integer syslogSeverityValue;
    private String syslogSeverity;
    private Integer severityValue;
    private String severity;
    private Instant eventTime;

    @Column(columnDefinition = "TEXT")
    private String message;
}
