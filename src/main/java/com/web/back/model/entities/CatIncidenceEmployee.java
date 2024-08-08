package com.web.back.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "cat_incidence_employee")
public class CatIncidenceEmployee {
    @EmbeddedId
    private CatIncidenceEmployeeId id;

    @MapsId("catIncidenceId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cat_incidence_id", nullable = false)
    private CatIncidence catIncidence;

}