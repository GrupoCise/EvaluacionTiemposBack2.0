package com.web.back.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "change_log")
public class ChangeLog {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "field", length = 30)
    private String field;

    @Column(name = "original")
    private String original;

    @Column(name = "updated")
    private String updated;

    @Column(name = "num_empleado", length = 20)
    private String numEmpleado;

    @Column(name = "evaluation_id")
    private Integer evaluationId;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "updated_on")
    private Instant updatedOn;

}
