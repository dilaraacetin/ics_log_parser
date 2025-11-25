package com.example.icslog.adapters.out.db;

import com.example.icslog.adapters.out.db.entity.SnmpEventEntity;
import com.example.icslog.adapters.out.db.repo.SnmpEventJpaRepository;
import com.example.icslog.application.port.out.SnmpEventStore;
import com.example.icslog.domain.model.SnmpEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaSnmpEventStoreAdapter implements SnmpEventStore {

    private final SnmpEventJpaRepository repo;

    @Override
    public SnmpEvent save(SnmpEvent event) {
        SnmpEventEntity e = new SnmpEventEntity();
        e.setId(event.id());
        e.setFrameTime(event.frameTime());
        e.setSrcIp(event.srcIp());
        e.setDstIp(event.dstIp());
        e.setCommunity(event.community());
        e.setVersion(event.version());
        e.setDataType(event.dataType());
        e.setOid(event.oid());
        e.setValue(event.value());
        e.setRawJson(event.rawJson());
        repo.save(e);
        return event;
    }
}
