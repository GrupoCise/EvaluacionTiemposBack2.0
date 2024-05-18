package com.web.back.repositories;

import com.web.back.model.entities.Evaluation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Optional<Evaluation> findById(Integer id);

    @Transactional
    default Evaluation updateOrInsert(Evaluation entity) {
        return save(entity);
    }

    @Query(
            value = "SELECT * FROM evaluation WHERE fecha BETWEEN :beginDate AND :endDate AND num_empleado = :numEmpleado",
            nativeQuery = true
    )
    List<Evaluation> findByFechaAndEmpleado(@Param("numEmpleado") String numEmpleado, @Param("beginDate") String beginDate, @Param("endDate") String endDate);

    @Query(
            value = "SELECT * FROM evaluation WHERE fecha BETWEEN :beginDate AND :endDate AND sociedad = :sociedad AND area_nomina = :areaNomina AND num_empleado = :numEmpleado LIMIT 1",
            nativeQuery = true
    )
    Optional<Evaluation> findByFechaAndAreaNominaAndSociedadAndEmpleado(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("sociedad") String sociedad, @Param("areaNomina") String areaNomina, @Param("numEmpleado") String numEmpleado);


    @Query(
            value = "SELECT * FROM evaluation WHERE fecha BETWEEN :beginDate AND :endDate AND sociedad = :sociedad AND area_nomina = :areaNomina order by fecha",
            nativeQuery = true
    )
    List<Evaluation> findByFechaAndAreaNominaAndSociedad(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("sociedad") String sociedad, @Param("areaNomina") String areaNomina);

}
