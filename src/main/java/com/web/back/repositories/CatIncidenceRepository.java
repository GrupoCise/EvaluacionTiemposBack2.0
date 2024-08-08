package com.web.back.repositories;

import com.web.back.model.entities.CatIncidence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CatIncidenceRepository extends JpaRepository<CatIncidence, Integer> {

    @Query("SELECT c FROM CatIncidence c WHERE c.sociedad = :sociedad AND c.areaNomina = :areaNomina")
    List<CatIncidence> findAllByIdSociedadAndAreaNomina(@Param("sociedad") String sociedad,
                                                              @Param("areaNomina") String areaNomina);
}
