package com.web.back.model.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cat_incidence")
public class CatIncidence {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 20)
    @Column(name = "mandt", length = 20)
    private String mandt;

    @Size(max = 50)
    @Column(name = "id_regla", length = 50)
    private String idRegla;

    @Size(max = 250)
    @Column(name = "descripcion", length = 250)
    private String descripcion;

    @Size(max = 50)
    @Column(name = "id_retorno", length = 50)
    private String idRetorno;

}