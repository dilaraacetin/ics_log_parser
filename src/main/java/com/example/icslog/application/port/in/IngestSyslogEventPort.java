package com.example.icslog.application.port.in;

import com.example.icslog.domain.model.SyslogEvent;

public interface IngestSyslogEventPort {
    void ingest(SyslogEvent event);
}
