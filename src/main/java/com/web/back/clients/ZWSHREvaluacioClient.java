package com.web.back.clients;

import com.web.back.model.requests.CambioHorarioRequest;
import com.web.back.model.responses.EmployeeApiResponse;
import com.web.back.model.responses.evaluacion.EvaluacionApiResponse;
import com.web.back.model.responses.CambioHorarioResponse;
import com.web.back.utils.DateUtil;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class ZWSHREvaluacioClient {

    private final WebClient webClient;
    private final String usuarioMotor;
    private final String passMotor;
    private final String sapClient;

    public ZWSHREvaluacioClient(@Value("${MOTOR-BASE-URI}") String motorBaseUrl,
                                @Value("${MOTOR-US}") String usuarioMotor,
                                @Value("${MOTOR-PASS}") String passMotor,
                                @Value("${SAP-CLIENT}") String sapClient) {
        this.usuarioMotor = usuarioMotor;
        this.passMotor = passMotor;
        this.sapClient = sapClient;

        var baseUrl = motorBaseUrl + "/zwshr_evaluacio";

        this.webClient = BaseWebClient.builder()
                .baseUrl(baseUrl).build();
    }

    public Mono<List<EmployeeApiResponse>> getEmployees(String username, String beginDate, String endDate, String sociedad, String areaNomina) {
        beginDate = DateUtil.clearSymbols(beginDate);
        endDate = DateUtil.clearSymbols(endDate);

        return webClient.get()
                .uri("/Empleados?I_PERNR=" + username + "&I_BEGDA=" + beginDate + "&I_ENDDA=" + endDate + "&SAP-CLIENT=" + sapClient + "&I_BUKRS=" + sociedad + "&I_ABKRS=" + areaNomina)
                .header("Authorization", getBasicAuthHeaderString())
                .header("X-CSRF-Token", "fetch")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(EmployeeApiResponse.class)
                .collectList();
    }

    public Mono<EvaluacionApiResponse> getEvaluacion(String username, String beginDate, String endDate) {
        beginDate = DateUtil.clearSymbols(beginDate);
        endDate = DateUtil.clearSymbols(endDate);

        return webClient.get()
                .uri("/Evaluacion?sap-client=" + sapClient + "&I_PERNR=" + username + "&I_BEGDA=" + beginDate + "&I_ENDDA=" + endDate)
                .header("Authorization", getBasicAuthHeaderString())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(EvaluacionApiResponse.class);
    }

    public Mono<List<CambioHorarioResponse>> postCambioHorario(String beginDate, String endDate, List<CambioHorarioRequest> cambioHorarioRequests) {
        beginDate = DateUtil.clearSymbols(beginDate);
        endDate = DateUtil.clearSymbols(endDate);

        //TODO: How to do this correctly

        var response = webClient.get()
                .uri("/Empleados?I_PERNR=" + "" + "&I_BEGDA=" + beginDate + "&I_ENDDA=" + endDate + "&SAP-CLIENT=" + sapClient + "&I_BUKRS=" + "" + "&I_ABKRS=" + "")
                .header("Authorization", getBasicAuthHeaderString())
                .header("X-CSRF-Token", "fetch")
                .retrieve()
                .toBodilessEntity().map(HttpEntity::getHeaders).block();

        List<String> cookies = response.get("Set-Cookie");
        String csrfToken = response.getFirst("X-CSRF-Token");

        return webClient.post()
                .uri("/CambioHorario?" + "I_BEGDA=" + beginDate + "&I_ENDDA=" + endDate)
                .header("Authorization", getBasicAuthHeaderString())
                .header("X-CSRF-Token", csrfToken)
                .header("Cookie", cookies.get(2))
                .bodyValue(cambioHorarioRequests)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity().map(result -> { result.getHeaders(); return List.of();});
//                .bodyToMono(new ParameterizedTypeReference<>() {
//                });
    }

    private String getBasicAuthHeaderString() {
        String authHeader = usuarioMotor + ":" + passMotor;
        byte[] authHeaderBytes = authHeader.getBytes();
        byte[] base64AuthHeader = Base64.encodeBase64(authHeaderBytes, false);
        String base64AuthHeaderString = new String(base64AuthHeader);

        return "Basic " + base64AuthHeaderString;
    }
}
