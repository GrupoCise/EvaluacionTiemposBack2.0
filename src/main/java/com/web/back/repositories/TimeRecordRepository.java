package com.web.back.repositories;

import com.web.back.model.entities.TimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface TimeRecordRepository extends JpaRepository<TimeRecord, UUID> {
    List<TimeRecord> findAllByDateBetween(Instant dateAfter, Instant dateBefore);
}
