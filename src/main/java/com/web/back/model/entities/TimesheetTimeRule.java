package com.web.back.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "timesheet_time_rule")
public class TimesheetTimeRule {
    @EmbeddedId
    private TimesheetTimeRuleId id;

    @MapsId("timesheetId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "timesheet_id", nullable = false)
    private Timesheet timesheet;

    @MapsId("timeRuleId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "time_rule_id", nullable = false)
    private TimeRule timeRule;

}
