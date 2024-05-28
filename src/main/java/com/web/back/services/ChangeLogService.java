package com.web.back.services;

import com.web.back.mappers.ChangeLogDtoMapper;
import com.web.back.mappers.ChangeLogXlsxMapper;
import com.web.back.model.dto.ChangeLogDto;
import com.web.back.model.entities.ChangeLog;
import com.web.back.model.requests.ChangeLogRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.xls.ChangeLogXlsx;
import com.web.back.model.xls.interfaces.XlsxWriter;
import com.web.back.repositories.ChangeLogRepository;
import com.web.back.repositories.EvaluationRepository;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
public class ChangeLogService {
    private final ChangeLogRepository changeLogRepository;
    private final EvaluationRepository evaluationRepository;
    private final XlsxWriter xlsxWriter;

    public ChangeLogService(ChangeLogRepository changeLogRepository, EvaluationRepository evaluationRepository, XlsxWriter xlsxWriter) {
        this.changeLogRepository = changeLogRepository;
        this.evaluationRepository = evaluationRepository;
        this.xlsxWriter = xlsxWriter;
    }

    public CustomResponse<List<ChangeLogDto>> getLogs(String beginDate, String endDate, String sociedad, String areaNomina) {

        var logs = changeLogRepository.findByFechaAndSociedadAndArea(beginDate, endDate, sociedad, areaNomina);

        var evaluationList = logs.stream().map(ChangeLog::getEvaluationId).distinct().toList();

        var evaluations = evaluationRepository.findAllById(evaluationList);

        var enrichedLogs = logs.stream().map(log -> ChangeLogDtoMapper.mapFrom(log, evaluations)).toList();

        return new CustomResponse<List<ChangeLogDto>>().ok(enrichedLogs);
    }

    public byte[] getLogsXlsData(ChangeLogRequest request) {
        var logs = changeLogRepository.findByFechaAndSociedadAndArea(
                request.beginDate(), request.endDate(), request.sociedad(), request.areaNomina());

        var changeLogXlsxes = logs.stream().map(ChangeLogXlsxMapper::mapFrom).toList();

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (bos; Workbook workbook = new XSSFWorkbook()) {
            String[] columnTitles = ChangeLogXlsx.getColumnTitles();
            xlsxWriter.write(changeLogXlsxes, bos, columnTitles, workbook);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return bos.toByteArray();
    }

    public void LogUpdateEvaluationsChanges(List<ChangeLog> changesSummary){
        changeLogRepository.saveAll(changesSummary);
    }
}
