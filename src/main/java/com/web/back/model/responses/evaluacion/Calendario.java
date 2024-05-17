package com.web.back.model.responses.evaluacion;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Calendario {
    private String bukrs;
    private String abkrs;
    private int pabrp;
    private int pabrj;
    private String begda;
    private String endda;
    private String cerrado;
}
