package com.web.back.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "time_records")
public class TimeRecord {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "id", nullable = false, length = 36)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @Column(name = "turn", nullable = false)
    private Integer turn;

    @Column(name = "entry_time")
    private LocalTime entryTime;

    @Column(name = "break_departure_time")
    private LocalTime breakDepartureTime;

    @Column(name = "break_return_time")
    private LocalTime breakReturnTime;

    @Column(name = "departure_time")
    private LocalTime departureTime;

    @Column(name = "date")
    private Instant date;
}
