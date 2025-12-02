package com.example.icslog.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.icslog.application.port.in.IngestModbusEventPort;
import com.example.icslog.application.port.out.ModbusEventStore;
import com.example.icslog.domain.model.ModbusEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class IngestModbusEventService implements IngestModbusEventPort {

    private final ModbusEventStore store;

    @Override
    public void ingest(ModbusEvent event) {
        System.out.println("Ingesting MODBUS from: " + event.srcIp()
                + " fc=" + event.functionCode()
                + " (" + event.functionName() + ")");
        store.save(event);
    }
}
