package com.web.back.repositories;

import com.web.back.model.entities.Period;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodRepository extends JpaRepository<Period, Long>  {
}
