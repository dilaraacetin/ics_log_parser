package com.example.icslog.adapters.out.db.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.icslog.adapters.out.db.entity.EventEntity;

@Repository
public interface EventJpaRepository extends JpaRepository<EventEntity, UUID> {
}
