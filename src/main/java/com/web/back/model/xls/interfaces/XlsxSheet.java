package com.web.back.model.xls.interfaces;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface XlsxSheet {
    String value();
}
