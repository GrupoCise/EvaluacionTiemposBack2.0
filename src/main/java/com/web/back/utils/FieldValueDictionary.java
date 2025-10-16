package com.web.back.utils;

import com.web.back.model.dto.FieldValue;
import com.web.back.model.dto.evaluation.TheoreticalTimesheetDay;
import com.web.back.model.entities.TimeRecord;
import lombok.Getter;

import java.util.List;

public final class FieldValueDictionary {
    @Getter
    private static List<FieldValue> dictionary = List.of();

    private FieldValueDictionary() {
        dictionary = List.of(
                new FieldValue("HRE", "entryTime", TimeRecord.class),
                new FieldValue("HREP", "breakDepartureTime", TimeRecord.class),
                new FieldValue("HRSP", "breakReturnTime", TimeRecord.class),
                new FieldValue("HRS", "departureTime", TimeRecord.class),
                new FieldValue("FP", "date", TimeRecord.class),
                new FieldValue("HE", "entryTime", TheoreticalTimesheetDay.class),
                new FieldValue("HEP", "breakDepartureTime", TheoreticalTimesheetDay.class),
                new FieldValue("HSP", "breakReturnTime", TheoreticalTimesheetDay.class),
                new FieldValue("HS", "departureTime", TheoreticalTimesheetDay.class)
//                new FieldValue("IDFE", "FTKLA", ls_t_teoricos.getFtkla()),
//                new FieldValue("TIPO", "TPROG", ls_t_teoricos.getTipo()),
//                new FieldValue("RIDFE", "FTKLA", lc_fest),
//                new FieldValue("RTIPO", "TPROG", lc_desc),
//                new FieldValue("SUMA", "NUM1", lv_sum),
//                new FieldValue("REG", "CHAR1", ls_res.getEstatusGen()),
//                new FieldValue("FDF", "SYST_DATUM", ls_df_ani.getCumple()),
//                new FieldValue("FDA", "SYST_DATUM", ls_df_ani.getAniversario()),
//                new FieldValue("TURN", "INT1", ls_res.getTurno())
        );
    }
}
