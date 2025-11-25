package com.example.icslog.adapters.out.db;

import com.example.icslog.adapters.out.db.entity.SyslogEventEntity;
import com.example.icslog.adapters.out.db.repo.SyslogEventJpaRepository;
import com.example.icslog.application.port.out.SyslogEventStore;
import com.example.icslog.domain.model.SyslogEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaSyslogEventStoreAdapter implements SyslogEventStore {

    private final SyslogEventJpaRepository repo;

    @Override
    public SyslogEvent save(SyslogEvent event) {
        SyslogEventEntity e = new SyslogEventEntity();
        e.setId(event.id());
        e.setMessageSourceAddress(event.messageSourceAddress());
        e.setEventReceivedTime(event.eventReceivedTime());
        e.setSourceModuleName(event.sourceModuleName());
        e.setSourceModuleType(event.sourceModuleType());
        e.setHostname(event.hostname());
        e.setSyslogFacilityValue(event.syslogFacilityValue());
        e.setSyslogFacility(event.syslogFacility());
        e.setSyslogSeverityValue(event.syslogSeverityValue());
        e.setSyslogSeverity(event.syslogSeverity());
        e.setSeverityValue(event.severityValue());
        e.setSeverity(event.severity());
        e.setEventTime(event.eventTime());
        e.setMessage(event.message());

        repo.save(e);
        return event;
    }
}
