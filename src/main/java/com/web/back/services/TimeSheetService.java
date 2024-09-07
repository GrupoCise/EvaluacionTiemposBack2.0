package com.web.back.services;

import com.web.back.clients.ZWSHREvaluacioClient;
import com.web.back.model.requests.RegistroHorariosRequest;
import com.web.back.model.responses.RegistroHorariosResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TimeSheetService {

    private final ZWSHREvaluacioClient zwshrEvaluacioClient;

    public TimeSheetService(ZWSHREvaluacioClient zwshrEvaluacioClient) {
        this.zwshrEvaluacioClient = zwshrEvaluacioClient;
    }

    public List<RegistroHorariosResponse> registerTimeSheets(List<RegistroHorariosRequest> registroHorariosRequests) {
        return zwshrEvaluacioClient.postRegistroHorarios(registroHorariosRequests).block();
    }
}
