package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.zdtco.datafetch.typeconverter.MultiColFormRowTypeConverters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2017/12/16.
 */

@Entity(tableName = "MultiColumnForm")
public class MultiColumnForm {

    @PrimaryKey
    @NonNull
    public String formID;
    public String formName;
    public boolean isTestForm;

    @TypeConverters(MultiColFormRowTypeConverters.class)
    public List<MultiColFormRow> multiColFormRows;

    public MultiColumnForm(@NonNull String formID, String formName) {
        this.formID = formID;
        this.formName = formName;
        this.multiColFormRows = new ArrayList<>();
    }
}
