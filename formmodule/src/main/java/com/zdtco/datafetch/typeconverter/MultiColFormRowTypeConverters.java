package com.zdtco.datafetch.typeconverter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdtco.datafetch.data.MachineOwnedForm;
import com.zdtco.datafetch.data.MultiColFormRow;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by G1494458 on 2017/12/23.
 */

public class MultiColFormRowTypeConverters {
    @TypeConverter
    public static List<MultiColFormRow> stringToMultiColFormRows(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<MultiColFormRow>>() {}.getType();
        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String multiColFormRowsToString(List<MultiColFormRow> ownedForms) {
        return new Gson().toJson(ownedForms);
    }
}
