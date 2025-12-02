package com.example.icslog.application.port.in;

import com.example.icslog.domain.model.ModbusEvent;

public interface IngestModbusEventPort {
    void ingest(ModbusEvent event);
}
