package com.example.icslog.adapters.out.db;

import com.example.icslog.application.port.out.EventStore;
import com.example.icslog.domain.model.Event;
import com.example.icslog.adapters.out.db.entity.EventEntity;
import com.example.icslog.adapters.out.db.repo.EventJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaEventStoreAdapter implements EventStore {

    private final EventJpaRepository eventRepository;

    @Override
    public Event save(Event event) {
        EventEntity entity = toEntity(event);
        EventEntity savedEntity = eventRepository.save(entity);
        return toDomain(savedEntity);
    }

    private EventEntity toEntity(Event event) {
        EventEntity entity = new EventEntity();
        entity.setId(event.id());
        entity.setDeviceId(event.deviceId());
        entity.setTimestamp(event.timestamp());
        entity.setSeverity(event.severity() != null ? event.severity() : com.example.icslog.domain.model.Severity.INFO);
        entity.setMessage(event.message());
        entity.setSourceIp(event.sourceIp());
        return entity;
    }

    private Event toDomain(EventEntity entity) {
        return new Event(
                entity.getId(),
                entity.getDeviceId(),
                entity.getTimestamp(),
                entity.getSeverity(),
                entity.getMessage(),
                entity.getSourceIp()
        );
    }
}
