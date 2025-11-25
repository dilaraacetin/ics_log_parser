package com.example.icslog.application.port.in;

import com.example.icslog.domain.model.SnmpEvent;

public interface IngestSnmpEventPort {
    void ingest(SnmpEvent event);
}
