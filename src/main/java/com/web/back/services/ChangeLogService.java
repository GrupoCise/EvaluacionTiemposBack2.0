package com.web.back.services;

import com.web.back.mappers.ChangeLogXlsxMapper;
import com.web.back.model.entities.ChangeLog;
import com.web.back.model.requests.ChangeLogRequest;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.xls.ChangeLogXlsx;
import com.web.back.model.xls.interfaces.XlsxWriter;
import com.web.back.repositories.ChangeLogRepository;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Service
public class ChangeLogService {
    private final ChangeLogRepository changeLogRepository;
    private final XlsxWriter xlsxWriter;

    public ChangeLogService(ChangeLogRepository changeLogRepository, XlsxWriter xlsxWriter) {
        this.changeLogRepository = changeLogRepository;
        this.xlsxWriter = xlsxWriter;
    }

    public CustomResponse<List<ChangeLog>> getLogs(String beginDate, String endDate, String sociedad, String areaNomina) {

        var logs = changeLogRepository.findByFechaAndSociedadAndArea(beginDate, endDate, sociedad, areaNomina);

        return new CustomResponse<List<ChangeLog>>().ok(logs);
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
