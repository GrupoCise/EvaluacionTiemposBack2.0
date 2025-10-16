package com.web.back.repositories;

import com.web.back.model.entities.TimesheetTimeRule;
import com.web.back.model.entities.TimesheetTimeRuleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TimesheetTimeRuleRepository extends JpaRepository<TimesheetTimeRule, TimesheetTimeRuleId> {
    List<TimesheetTimeRule> findAllByTimesheetIdIn(List<UUID> timesheetIds);
    void deleteAllByTimesheetId(UUID timesheetId);
    void deleteAllByTimeRuleId(UUID timeRuleId);
    List<TimesheetTimeRule> findAllByTimesheetId(UUID timesheetId);
    List<TimesheetTimeRule> findAllByTimeRuleId(UUID timeRuleId);
}
