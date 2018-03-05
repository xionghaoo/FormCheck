package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.zdtco.datafetch.typeconverter.FormPrintRowConverters;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2018/1/4.
 */

@Entity(tableName = "FormPrintData")
public class FormPrintData {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NotNull
    public String machineID;
    @NotNull
    public String formID;

    public String workNo;
    public String formType;
    public String startTime;
    public String endTime;
    public String clsr;   //班别
    public String errorMsg;
    public String comment;    //备注
    public boolean hasPost;
    public String postData;

    @Ignore
    public boolean hasChecked;

    @Ignore
    public boolean hasDeleted;

    @TypeConverters({FormPrintRowConverters.class})
    public List<FormPrintRow> printRows;

    public FormPrintData(@NotNull String formID, @NotNull String machineID, String workNo, String formType, String startTime, String endTime, String clsr, String comment) {
        this.workNo = workNo;
        this.formID = formID;
        this.machineID = machineID;
        this.formType = formType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.clsr = clsr;
        this.comment = comment;
        printRows = new ArrayList<>();
    }
}
