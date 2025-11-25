package com.example.icslog.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.icslog.application.port.in.IngestSyslogEventPort;
import com.example.icslog.application.port.out.SyslogEventStore;
import com.example.icslog.domain.model.SyslogEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class IngestSyslogEventService implements IngestSyslogEventPort {

    private final SyslogEventStore store;

    @Override
    public void ingest(SyslogEvent event) {
        System.out.println("Ingesting SYSLOG from: " + event.messageSourceAddress()
                + " severity=" + event.syslogSeverity());
        store.save(event);
    }
}
