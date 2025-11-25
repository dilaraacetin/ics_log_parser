package com.example.icslog.application.port.out;

import com.example.icslog.domain.model.SnmpEvent;

public interface SnmpEventStore {
    SnmpEvent save(SnmpEvent event);
}
