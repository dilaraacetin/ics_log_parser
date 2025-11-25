package com.example.icslog.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.icslog.application.port.in.IngestSnmpEventPort;
import com.example.icslog.application.port.out.SnmpEventStore;
import com.example.icslog.domain.model.SnmpEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class IngestSnmpEventService implements IngestSnmpEventPort {

    private final SnmpEventStore store;

    @Override
    public void ingest(SnmpEvent event) {
        System.out.println("Ingesting SNMP from: " + event.srcIp()
                + " oid=" + event.oid());
        store.save(event);
    }
}
