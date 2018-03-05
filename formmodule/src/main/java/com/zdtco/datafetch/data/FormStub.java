package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;

import com.zdtco.datafetch.typeconverter.FormStubRowConverters;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2018/1/10.
 */

@Entity(tableName = "FormStub", primaryKeys = {"machineID", "formID", "mergeIndex"})
public class FormStub {
    @NotNull
    public String machineID;

    @NotNull
    public String formID;

    public int mergeIndex = 0;

    public String comment;

    @TypeConverters(FormStubRowConverters.class)
    public List<FormRowStub> rowStubs;

    public FormStub(@NotNull String machineID, @NotNull String formID, String comment) {
        this.machineID = machineID;
        this.formID = formID;
        this.comment = comment;
        rowStubs = new ArrayList<>();
    }
}
