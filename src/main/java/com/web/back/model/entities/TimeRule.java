package com.web.back.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "time_rules")
public class TimeRule {
    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "id", nullable = false, length = 36)
    @JdbcTypeCode(SqlTypes.CHAR)
    private UUID id;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @Column(name = "level")
    private Integer level;

    @Column(name = "sequence")
    private Integer sequence;

    @Size(max = 255)
    @Column(name = "rule")
    private String rule;

    @Size(max = 10)
    @Column(name = "result_meets")
    private String resultMeets;

    @Column(name = "exclusive")
    private boolean exclusive;

}
