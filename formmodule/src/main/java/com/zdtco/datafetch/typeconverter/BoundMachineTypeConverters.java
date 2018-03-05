package com.zdtco.datafetch.typeconverter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zdtco.datafetch.data.BoundMachine;
import com.zdtco.datafetch.data.MachineOwnedForm;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

/**
 * Created by G1494458 on 2017/12/23.
 */

public class BoundMachineTypeConverters {
    @TypeConverter
    public static List<BoundMachine> stringToBoundMachines(String data) {
        if (data == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<BoundMachine>>() {}.getType();
        return new Gson().fromJson(data, listType);
    }

    @TypeConverter
    public static String boundMachinesToString(List<BoundMachine> boundMachines) {
        return new Gson().toJson(boundMachines);
    }
}
