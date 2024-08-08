package com.web.back.repositories;

import com.web.back.model.entities.CatIncidenceEmployee;
import com.web.back.model.entities.CatIncidenceEmployeeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CatIncidenceEmployeeRepository extends JpaRepository<CatIncidenceEmployee, CatIncidenceEmployeeId> {
    @Query("SELECT cie FROM CatIncidenceEmployee cie WHERE cie.id.employeeNum IN :employeeNums")
    List<CatIncidenceEmployee> findAllByEmployeeNums(@Param("employeeNums") List<String> employeeNums);
}
