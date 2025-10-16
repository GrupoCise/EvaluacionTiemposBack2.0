package com.web.back.repositories;

import com.web.back.model.entities.TimeRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TimeRuleRepository extends JpaRepository<TimeRule, UUID> {
}
