package com.web.back.model.xls;

import com.web.back.model.xls.interfaces.XlsxSingleField;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class EmployeeIncidenceXlsx {
    @XlsxSingleField(columnIndex = 0)
    private String employeeNumber;

    @XlsxSingleField(columnIndex = 1)
    private String employeeName;

    @XlsxSingleField(columnIndex = 2)
    private String fecha;

    public static String[] getColumnTitles(){
        return new String[]{
                "Numero de empleado",
                "Nombre del empleado",
                "Fecha de incidencia"};
    }
}
