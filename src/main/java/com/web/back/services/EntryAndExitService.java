package com.web.back.services;

import com.web.back.clients.ZWSHREvaluacioClient;
import com.web.back.model.requests.EntryExitMarkRequest;
import com.web.back.model.responses.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EntryAndExitService {
    private final ZWSHREvaluacioClient zwshrEvaluacioClient;

    public EntryAndExitService(ZWSHREvaluacioClient zwshrEvaluacioClient) {
        this.zwshrEvaluacioClient = zwshrEvaluacioClient;
    }

    public CustomResponse<Void> registerEntryAndExitMarks(List<EntryExitMarkRequest> entryAndExitMarks){
        return zwshrEvaluacioClient.registerEntryAndExitMarks(entryAndExitMarks)
                .map(this::manageEntryAndExitResponse).block();
    }

    private CustomResponse<Void> manageEntryAndExitResponse(ResponseEntity<Void> responseEntity) {
        return switch (responseEntity.getStatusCode().value()) {
            case 200, 201 -> new CustomResponse<Void>().ok(null, "Successful Marcajes guardados en SAP");
            case 400 -> new CustomResponse<Void>().badRequest("Datos erróneos en el cuerpo del request");
            case 401 -> new CustomResponse<Void>().unAuthorized("Autorización faltante o errónea");
            default -> new CustomResponse<Void>().internalError("Error interno en el servidor");
        };
    }
}
