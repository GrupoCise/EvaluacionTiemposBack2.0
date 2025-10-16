package com.web.back.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Time;
import java.time.LocalDate;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "evaluation")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private Time horaEntrada;

    @Column(name = "hora_pausa")
    private Time horaPausa;

    @Column(name = "hora_regreso_pausa")
    private Time horaRegresoPausa;

    @Column(name = "hora_salida")
    private Time horaSalida;

    @Column(name = "resultado_entrada", length = 5)
    private String resultadoEntrada;

    @Column(name = "resultado_pausa", length = 5)
    private String resultadoPausa;

    @Column(name = "resultado_regreso_pausa", length = 5)
    private String resultadoRegresoPausa;

    @Column(name = "resultado_salida", length = 5)
    private String resultadoSalida;

    @Column(name = "resultado_general", length = 250)
    private String resultadoGeneral;

    @Column(name = "status_registro", length = 20)
    private String statusRegistro;

    @Column(name = "num_empleado", length = 20)
    private String numEmpleado;

    @Column(name = "horario", length = 8)
    private String horario;

    @Column(name = "comentario", length = 80)
    private String comentario;

    @Column(name = "enlace")
    private String enlace;

    @ColumnDefault("0")
    @Column(name = "horas_extra")
    private Short horasExtra;

    @ColumnDefault("0")
    @Column(name = "horas_tomadas")
    private Short horasTomadas;

    @Column(name = "payload")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> payload;

    @Size(max = 20)
    @Column(name = "area_nomina", length = 20)
    private String areaNomina;

    @Size(max = 20)
    @Column(name = "sociedad", length = 20)
    private String sociedad;

    @Size(max = 20)
    @Column(name = "tipo_hrs_extra", length = 20)
    private String tipoHrsExtra;

    @Column(name = "tipo_incidencia")
    private Integer tipoIncidencia;

    @Size(max = 255)
    @Column(name = "referencia")
    private String referencia;

    @Size(max = 255)
    @Column(name = "consecutivo1")
    private String consecutivo1;

    @Size(max = 255)
    @Column(name = "consecutivo2")
    private String consecutivo2;

    @Column(name = "approbation_level")
    private Integer approbationLevel;

    @ColumnDefault("b'0'")
    @Column(name = "aprobado")
    private Boolean aprobado;

    @Column(name = "turn")
    private Integer turn;

    @Size(max = 350)
    @Column(name = "employee_name", length = 350)
    private String employeeName;

    @Size(max = 100)
    @Column(name = "payroll", length = 100)
    private String payroll;

    public void addPropertyPayload(String key, Object value) {
        this.payload.put(key, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Evaluation that = (Evaluation) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
