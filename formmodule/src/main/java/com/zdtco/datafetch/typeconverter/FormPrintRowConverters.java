package com.zdtco.datafetch.typeconverter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdtco.datafetch.data.FormPrintRow;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by G1494458 on 2017/12/23.
 */

public class FormPrintRowConverters {
    @TypeConverter
    public static List<FormPrintRow> stringToFormPrintRows(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<FormPrintRow>>() {}.getType();
        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String FormPrintRowsToString(List<FormPrintRow> ownedForms) {
        return new Gson().toJson(ownedForms);
    }
}
