package com.example.icslog.adapters.out.db.repo;

import com.example.icslog.adapters.out.db.entity.SyslogEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SyslogEventJpaRepository extends JpaRepository<SyslogEventEntity, UUID> {
}
