package com.web.back.controllers;

import com.web.back.filters.PermissionsFilter;
import com.web.back.model.responses.CustomResponse;
import com.web.back.model.responses.evaluacion.EvaluacionApiResponse;
import com.web.back.services.FiltersService;
import com.web.back.services.JwtService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/filters/")
@Tag(name = "Filters")
public class FiltersController {
    private final JwtService jwtService;
    private final FiltersService filtersService;

    public FiltersController(JwtService jwtService, FiltersService filtersService) {
        this.jwtService = jwtService;
        this.filtersService = filtersService;
    }

    @GetMapping(value="getFilerData")
    public Mono<CustomResponse<EvaluacionApiResponse>> getFilterDataS(@RequestHeader("Authorization") String bearerToken)
    {
        if (!PermissionsFilter.canRead(jwtService.getPermissionsFromToken(bearerToken))) {
            return Mono.just(new CustomResponse<EvaluacionApiResponse>().forbidden());
        }

        String username = jwtService.getUsernameFromToken(bearerToken);

        return filtersService.getFilters(username)
                .map(filters -> new CustomResponse<EvaluacionApiResponse>().ok(filters));
    }
}
