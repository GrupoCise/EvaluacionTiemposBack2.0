package com.web.back.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class CatIncidenceEmployeeId implements java.io.Serializable {
    private static final long serialVersionUID = 3010204408125649707L;
    @NotNull
    @Column(name = "cat_incidence_id", nullable = false)
    private Integer catIncidenceId;

    @Size(max = 100)
    @NotNull
    @Column(name = "employee_num", nullable = false, length = 100)
    private String employeeNum;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CatIncidenceEmployeeId entity = (CatIncidenceEmployeeId) o;
        return Objects.equals(this.employeeNum, entity.employeeNum) &&
                Objects.equals(this.catIncidenceId, entity.catIncidenceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(employeeNum, catIncidenceId);
    }

}