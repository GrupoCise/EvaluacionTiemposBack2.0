package com.web.back.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "timesheets")
public class Timesheet {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM) // For Hibernate 6+
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "CHAR(36)")
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Size(max = 5)
    @NotNull
    @Column(name = "timesheet_identifier", nullable = false, length = 5)
    private String timesheetIdentifier;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "days_of_the_week", nullable = false)
    private Short daysOfTheWeek;

    @Column(name = "entry_time")
    private LocalTime entryTime;

    @Column(name = "break_departure_time")
    private LocalTime breakDepartureTime;

    @Column(name = "break_return_time")
    private LocalTime breakReturnTime;

    @Column(name = "departure_time")
    private LocalTime departureTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Timesheet timesheet = (Timesheet) o;
        return id != null && id.equals(timesheet.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

}
