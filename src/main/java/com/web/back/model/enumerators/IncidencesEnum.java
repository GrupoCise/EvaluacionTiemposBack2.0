package com.web.back.model.enumerators;

import lombok.Getter;

@Getter
public enum IncidencesEnum {
    VACATIONS("VAC"),
    INCAPACITY("INCAP");

    private final String value;

    IncidencesEnum(final String enumValue) {
        value = enumValue;
    }

}
