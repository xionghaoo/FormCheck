package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.zdtco.datafetch.typeconverter.GeneralRowTypeConverters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2017/12/23.
 */
@Entity(tableName = "GeneralForm")
public class GeneralForm {

    @NonNull
    @PrimaryKey
    public String id;
    public String name;
    public int templateType;
    public boolean isTestPostForm;

    @TypeConverters(GeneralRowTypeConverters.class)
    public List<GeneralRow> generalRows;

    public GeneralForm(@NonNull String id, String name) {
        this.id = id;
        this.name = name;
        generalRows = new ArrayList<>();

    }
}
