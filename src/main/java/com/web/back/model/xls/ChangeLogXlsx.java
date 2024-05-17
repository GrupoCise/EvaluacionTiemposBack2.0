package com.web.back.model.xls;

import com.web.back.model.xls.interfaces.XlsxSheet;
import com.web.back.model.xls.interfaces.XlsxSingleField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@XlsxSheet(value = "Logs")
public class ChangeLogXlsx {

    @XlsxSingleField(columnIndex = 0)
    private String numEmpleado;

    @XlsxSingleField(columnIndex = 1)
    private Instant updatedOn;

    @XlsxSingleField(columnIndex = 2)
    private String field;

    @XlsxSingleField(columnIndex = 3)
    private String original;

    @XlsxSingleField(columnIndex = 4)
    private String updated;

    @XlsxSingleField(columnIndex = 5)
    private Integer userId;

    public static String[] getColumnTitles(){
        return new String[]{
                "Numero de empleado",
                "Fecha de modificacion",
                "Dato modificado",
                "Valor original",
                "Valor nuevo",
                "Usuario que modifico"};
    }
}
