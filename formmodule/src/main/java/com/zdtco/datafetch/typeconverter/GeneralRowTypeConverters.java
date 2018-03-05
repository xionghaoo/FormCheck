package com.zdtco.datafetch.typeconverter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdtco.datafetch.data.GeneralForm;
import com.zdtco.datafetch.data.GeneralRow;
import com.zdtco.datafetch.data.MachineOwnedForm;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by G1494458 on 2017/12/23.
 */

public class GeneralRowTypeConverters {
    @TypeConverter
    public static List<GeneralRow> stringToGeneralRows(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<GeneralRow>>() {}.getType();
        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String generalRowsToString(List<GeneralRow> ownedForms) {
        return new Gson().toJson(ownedForms);
    }
}
