package com.example.icslog.application.port.out;

import com.example.icslog.domain.model.Event;

public interface EventStore {
    Event save(Event event);
}
