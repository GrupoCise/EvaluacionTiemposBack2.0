package com.web.back.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "change_log")
public class ChangeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Column(name = "updated_on")
    private Instant updatedOn;

    @Size(max = 250)
    @Column(name = "sociedad", length = 250)
    private String sociedad;

    @Size(max = 250)
    @Column(name = "area_nomina", length = 250)
    private String areaNomina;

    @Size(max = 50)
    @Column(name = "editor_user_name", length = 50)
    private String editorUserName;

    @Size(max = 250)
    @Column(name = "editor_name", length = 250)
    private String editorName;

    @Size(max = 250)
    @Column(name = "empleado_name", length = 250)
    private String empleadoName;


}
