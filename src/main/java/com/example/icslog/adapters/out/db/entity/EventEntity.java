package com.example.icslog.adapters.out.db.entity;

import java.time.Instant;
import java.util.UUID;

import com.example.icslog.domain.model.Severity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
public class EventEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private Instant timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity = Severity.INFO;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String sourceIp;
}
