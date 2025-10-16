package com.web.back.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Embeddable
public class TimesheetTimeRuleId implements Serializable {
    @Serial
    private static final long serialVersionUID = -8559190784158775883L;

    @Size(max = 36)
    @NotNull
    @Column(name = "timesheet_id", nullable = false, length = 36)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID timesheetId;

    @Size(max = 36)
    @NotNull
    @Column(name = "time_rule_id", nullable = false, length = 36)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID timeRuleId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        TimesheetTimeRuleId entity = (TimesheetTimeRuleId) o;
        return Objects.equals(this.timesheetId, entity.timesheetId) &&
                Objects.equals(this.timeRuleId, entity.timeRuleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timesheetId, timeRuleId);
    }

}
