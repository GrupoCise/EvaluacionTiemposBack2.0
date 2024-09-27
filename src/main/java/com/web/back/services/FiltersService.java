package com.web.back.services;

import com.web.back.clients.ZWSHREvaluacioClient;
import com.web.back.model.responses.evaluacion.EvaluacionApiResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Service
public class FiltersService {
    private final ZWSHREvaluacioClient zwshrEvaluacioClient;

    public FiltersService(ZWSHREvaluacioClient zwshrEvaluacioClient) {
        this.zwshrEvaluacioClient = zwshrEvaluacioClient;
    }

    public Mono<EvaluacionApiResponse> getFilters(String userName) {
        String currentDate = LocalDate.now().toString();

        try{

            return zwshrEvaluacioClient.getEvaluacion(userName, currentDate, currentDate);
        } catch (Exception e) {
            return null;
        }
    }
}
