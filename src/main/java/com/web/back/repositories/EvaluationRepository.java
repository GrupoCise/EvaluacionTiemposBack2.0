package com.web.back.repositories;

import com.web.back.model.entities.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Integer> {
    @Query(
            value = "SELECT * FROM evaluation WHERE CAST(fecha AS DATE) BETWEEN :beginDate AND :endDate AND num_empleado = :numEmpleado",
            nativeQuery = true
    )
    List<Evaluation> findByFechaAndEmpleado(@Param("numEmpleado") String numEmpleado, @Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(
            value = "SELECT * FROM evaluation WHERE fecha LIKE :fecha% AND horario = :horario AND (:turn IS NULL OR turn = :turn) AND sociedad = :sociedad AND area_nomina = :areaNomina AND num_empleado = :numEmpleado LIMIT 1",
            nativeQuery = true
    )
    Optional<Evaluation> findByFechaAndHorarioAndTurnAndAreaNominaAndSociedadAndEmpleado(@Param("fecha") String fecha, @Param("horario") String horario, @Param("turn") Integer turn, @Param("sociedad") String sociedad, @Param("areaNomina") String areaNomina, @Param("numEmpleado") String numEmpleado);


    @Query(
            value = "SELECT * FROM evaluation WHERE CAST(fecha AS DATE) BETWEEN :beginDate AND :endDate AND sociedad = :sociedad AND area_nomina = :areaNomina order by fecha",
            nativeQuery = true
    )
    List<Evaluation> findByFechaAndAreaNominaAndSociedad(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("sociedad") String sociedad, @Param("areaNomina") String areaNomina);

    @Query(
            value = "SELECT * FROM evaluation WHERE CAST(fecha AS DATE) BETWEEN :beginDate AND :endDate AND (:turn IS NULL OR turn = :turn) order by fecha",
            nativeQuery = true
    )
    List<Evaluation> findByFechaAndTurn(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("turn") Integer turn);

    @Query(
            value = "SELECT * FROM evaluation WHERE num_empleado in :employee_numbers order by fecha",
            nativeQuery = true
    )
    List<Evaluation> findAllByEmployeeNumber(@Param("employee_numbers") List<String> employeeNumbers);

    List<Evaluation> findAllByFechaBetween(LocalDate beginDate, LocalDate endDate);
}
