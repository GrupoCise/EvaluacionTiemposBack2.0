package com.web.back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FieldValue {
    private String key;
    private String fieldName;
    private Class<?> sourceClass;
}
