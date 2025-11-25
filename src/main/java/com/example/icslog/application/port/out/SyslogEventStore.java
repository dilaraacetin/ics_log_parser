package com.example.icslog.application.port.out;

import com.example.icslog.domain.model.SyslogEvent;

public interface SyslogEventStore {
    SyslogEvent save(SyslogEvent event);
}
