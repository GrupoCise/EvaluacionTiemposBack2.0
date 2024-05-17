package com.web.back.model.xls.interfaces;

import java.lang.annotation.*;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface XlsxSingleField {
    int columnIndex();
}
