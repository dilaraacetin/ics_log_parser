package com.example.icslog.application.port.out;

import com.example.icslog.domain.model.ModbusEvent;

public interface ModbusEventStore {
    ModbusEvent save(ModbusEvent event);
}
