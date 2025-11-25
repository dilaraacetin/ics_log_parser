package com.example.icslog.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.icslog.application.port.in.IngestEventPort;
import com.example.icslog.application.port.out.EventStore;
import com.example.icslog.domain.model.Event;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class IngestEventService implements IngestEventPort {

    private final EventStore eventStore;

    @Override
    public void ingestEvent(Event event) {
        System.out.println("Ingesting event from device: " + event.deviceId());
        eventStore.save(event);
    }
}
