package com.web.back.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "evaluation")
public class Evaluation {
    @Id
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora_entrada")
    private LocalTime horaEntrada;

    @Column(name = "hora_pausa")
    private LocalTime horaPausa;

    @Column(name = "hora_regreso_pausa")
    private LocalTime horaRegresoPausa;

    @Column(name = "hora_salida")
    private LocalTime horaSalida;

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

    @Column(name = "incapacidad")
    private Integer incapacidad;

    @ColumnDefault("0")
    @Column(name = "aprobado")
    private Byte aprobado;

    @ColumnDefault("0")
    @Column(name = "horas_extra")
    private Short horasExtra;

    @ColumnDefault("0")
    @Column(name = "horas_tomadas")
    private Short horasTomadas;

    @Column(name = "payload")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Object> payload;

}
