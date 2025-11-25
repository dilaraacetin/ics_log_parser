package com.example.icslog.adapters.out.db.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.icslog.adapters.out.db.entity.SnmpEventEntity;

public interface SnmpEventJpaRepository extends JpaRepository<SnmpEventEntity, UUID> {
}
