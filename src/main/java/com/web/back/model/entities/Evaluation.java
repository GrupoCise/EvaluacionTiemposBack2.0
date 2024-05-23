package com.web.back.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.sql.Time;
import java.util.Date;
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
    private Date fecha;

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

    @Column(name = "status_registro", length = 2)
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
    @Column(name = "aprobado")
    private Boolean aprobado;

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

    public void addPropertyPayload(String key, Object value) {
        payload.put(key, value);
    }

    public Evaluation(){}

    public Evaluation(Date fecha, Integer id, Time horaEntrada, Time horaPausa, Time horaRegresoPausa, Time horaSalida, String resultadoEntrada, String resultadoPausa, String resultadoRegresoPausa, String resultadoSalida, String resultadoGeneral, String statusRegistro, String numEmpleado, String horario, String comentario, String enlace, Boolean aprobado, Short horasExtra, Short horasTomadas, Map<String, Object> payload, String areaNomina, String sociedad, String tipoHrsExtra) {
        this.fecha = fecha;
        this.id = id;
        this.horaEntrada = horaEntrada;
        this.horaPausa = horaPausa;
        this.horaRegresoPausa = horaRegresoPausa;
        this.horaSalida = horaSalida;
        this.resultadoEntrada = resultadoEntrada;
        this.resultadoPausa = resultadoPausa;
        this.resultadoRegresoPausa = resultadoRegresoPausa;
        this.resultadoSalida = resultadoSalida;
        this.resultadoGeneral = resultadoGeneral;
        this.statusRegistro = statusRegistro;
        this.numEmpleado = numEmpleado;
        this.horario = horario;
        this.comentario = comentario;
        this.enlace = enlace;
        this.aprobado = aprobado;
        this.horasExtra = horasExtra;
        this.horasTomadas = horasTomadas;
        this.payload = payload;
        this.areaNomina = areaNomina;
        this.sociedad = sociedad;
        this.tipoHrsExtra = tipoHrsExtra;
    }
}
