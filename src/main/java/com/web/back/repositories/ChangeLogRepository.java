package com.web.back.repositories;

import com.web.back.model.entities.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeLogRepository extends JpaRepository<ChangeLog, Integer> {
    @Query(
            value = "SELECT a.* FROM change_log a INNER JOIN evaluation e ON a.evaluation_id = e.id where CAST(e.fecha AS DATE) between :beginDate and :endDate AND a.sociedad = :sociedad AND a.area_nomina = :area AND a.editor_user_name = :editor_user_name order by a.updated_on;",nativeQuery = true
    )
    List<ChangeLog> findByFechaAndSociedadAndAreaAndEditor(@Param("beginDate") String beginDate, @Param("endDate") String endDate, @Param("sociedad") String sociedad, @Param("area") String area, @Param("editor_user_name") String editorUserName);
}
