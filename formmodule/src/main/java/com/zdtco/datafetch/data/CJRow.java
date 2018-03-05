package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.zdtco.datafetch.typeconverter.GeneralRowTypeConverters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2018/1/2.
 */

@Entity(tableName = "CJRow")
public class CJRow {

    @NonNull
    @PrimaryKey
    public String id;

    @TypeConverters(GeneralRowTypeConverters.class)
    public List<GeneralRow> generalRows;

    public CJRow(@NonNull String id) {
        this.id = id;
        generalRows = new ArrayList<>();
    }
}
