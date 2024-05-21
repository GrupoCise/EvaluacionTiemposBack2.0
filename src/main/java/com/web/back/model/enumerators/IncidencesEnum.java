package com.web.back.model.enumerators;

import lombok.Getter;

@Getter
public enum IncidencesEnum {
    VACATIONS(1),
    INCAPACITY(2);

    private final int value;

    IncidencesEnum(final int enumValue) {
        value = enumValue;
    }

}
