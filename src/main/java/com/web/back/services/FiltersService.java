package com.web.back.services;

import com.web.back.clients.ZWSHREvaluacioClient;
import com.web.back.model.responses.evaluacion.EvaluacionApiResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.sql.Date;

@Service
public class FiltersService {
    private final ZWSHREvaluacioClient zwshrEvaluacioClient;

    public FiltersService(ZWSHREvaluacioClient zwshrEvaluacioClient) {
        this.zwshrEvaluacioClient = zwshrEvaluacioClient;
    }

    public Mono<EvaluacionApiResponse> getFilters(String userName) {
        String currentDate = new Date(System.currentTimeMillis()).toString();

        try{
            var r = zwshrEvaluacioClient.getEvaluacion(userName, currentDate, currentDate);

            return r;
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }
}
