package com.web.back.services;

import com.web.back.clients.ZWSHREvaluacioService;
import com.web.back.model.responses.evaluacion.EvaluacionApiResponse;
import com.web.back.utils.DateUtil;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.sql.Date;

@Service
public class FiltersService {
    private final ZWSHREvaluacioService zwshrEvaluacioService;

    public FiltersService(ZWSHREvaluacioService zwshrEvaluacioService) {
        this.zwshrEvaluacioService = zwshrEvaluacioService;
    }

    public Mono<EvaluacionApiResponse> getFilters(String userName) {

        String currentDate = new Date(System.currentTimeMillis()).toString();
        currentDate = DateUtil.clearSymbols(currentDate);

        return zwshrEvaluacioService.getEvaluacion(userName, currentDate, currentDate);
    }
}
