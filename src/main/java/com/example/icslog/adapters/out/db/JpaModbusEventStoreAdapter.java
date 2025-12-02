package com.example.icslog.adapters.out.db;

import com.example.icslog.adapters.out.db.entity.ModbusEventEntity;
import com.example.icslog.adapters.out.db.repo.ModbusEventJpaRepository;
import com.example.icslog.application.port.out.ModbusEventStore;
import com.example.icslog.domain.model.ModbusEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaModbusEventStoreAdapter implements ModbusEventStore {

    private final ModbusEventJpaRepository repo;

    @Override
    public ModbusEvent save(ModbusEvent event) {
        ModbusEventEntity e = new ModbusEventEntity();
        e.setId(event.id());
        e.setFrameTime(event.frameTime());
        e.setSrcIp(event.srcIp());
        e.setDstIp(event.dstIp());
        e.setTransactionId(event.transactionId());
        e.setProtocolId(event.protocolId());
        e.setLengthField(event.lengthField());
        e.setUnitId(event.unitId());
        e.setFunctionCode(event.functionCode());
        e.setFunctionName(event.functionName());
        e.setRegisterAddress(event.registerAddress()); 
        e.setRegisterValue(event.registerValue());     
        e.setDataHex(event.dataHex());
        e.setRawJson(event.rawJson());

        repo.save(e);
        return event;
    }

}
