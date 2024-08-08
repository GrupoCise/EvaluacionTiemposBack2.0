package com.web.back.repositories;

import com.web.back.model.entities.CatIncidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CatIncidenceRepository extends JpaRepository<CatIncidence, Integer> {

    @Query("SELECT c FROM CatIncidence c WHERE c.idRegla IN :idReglas AND c.idRetorno IN :idRetornos AND c.mandt IN :mandts")
    List<CatIncidence> findAllByIdReglasAndIdRetornosAndMandt(@Param("idReglas") List<String> idReglas,
                                                              @Param("idRetornos") List<String> idRetornos,
                                                              @Param("mandts") List<String> mandts);

}
