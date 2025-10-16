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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public CustomResponse<List<ChangeLogDto>> getLogs(String beginDate, String endDate, String sociedad, String areaNomina, String editorUserName) {

        var logs = changeLogRepository.findByFechaAndSociedadAndAreaAndEditor(beginDate, endDate, sociedad, areaNomina, editorUserName);

        var evaluationList = logs.stream().map(ChangeLog::getEvaluationId).distinct().toList();

        var evaluations = evaluationRepository.findAllById(evaluationList);

        var enrichedLogs = logs.stream().map(log -> ChangeLogDtoMapper.mapFrom(log, evaluations)).toList();

        return new CustomResponse<List<ChangeLogDto>>().ok(enrichedLogs);
    }

    @Transactional
    public byte[] getLogsXlsData(ChangeLogRequest request, String editorUserName) {
        var logs = changeLogRepository.findByFechaAndSociedadAndAreaAndEditor(
                request.beginDate(), request.endDate(), request.sociedad(), request.areaNomina(), editorUserName);

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

    @Transactional
    public void LogUpdateEvaluationsChanges(List<ChangeLog> changesSummary){
        if (changesSummary == null || changesSummary.isEmpty()) return;

        changeLogRepository.saveAll(changesSummary);
    }
}
