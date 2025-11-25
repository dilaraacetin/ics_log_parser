package com.example.icslog.application.port.in;

import com.example.icslog.domain.model.Event;

public interface IngestEventPort {
    void ingestEvent(Event event);
}
