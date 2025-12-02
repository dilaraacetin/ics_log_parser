package com.example.icslog.adapters.out.db.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.icslog.adapters.out.db.entity.ModbusEventEntity;

public interface ModbusEventJpaRepository extends JpaRepository<ModbusEventEntity, UUID> {
}
