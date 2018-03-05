package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2018/1/4.
 */

public class FormPostData {
    @SerializedName("WorkNo")
    public String workNo;
    @SerializedName("reportcode")
    public String formID;
    @SerializedName("Equ_id")
    public String machineID;
    @SerializedName("modetype")
    public String formType;
    @SerializedName("StartTime")
    public String startTime;
    @SerializedName("EndTime")
    public String endTime;
    @SerializedName("class")
    public String clsr;   //班别
    @SerializedName("errorMessage")
    public String errorMsg;
    @SerializedName("Comment")
    public String comment;    //备注
    @SerializedName("FieldItems")
    public List<FormPostRow> postRows;

    public FormPostData() {
        postRows = new ArrayList<>();
    }

    public FormPostData(String workNo, String formID, String machineID, String formType, String startTime, String endTime, String clsr, String errorMsg, String comment) {
        this.workNo = workNo;
        this.formID = formID;
        this.machineID = machineID;
        this.formType = formType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.clsr = clsr;
        this.errorMsg = errorMsg;
        this.comment = comment;
        postRows = new ArrayList<>();
    }

    public static class FormPostRow {
        @SerializedName("key")
        public String rowName;
        @SerializedName("value")
        public String rowValue;
        @SerializedName("itemcode")
        public String rowID;
        @SerializedName("istop")
        public String rowExtraType;
        @SerializedName("controlType")
        public String controlType;

        @Expose(serialize = false, deserialize = false)
        public String orderNo;

        public FormPostRow(String rowName, String rowValue, String rowID, String rowExtraType, String controlType) {
            this.rowName = rowName;
            this.rowValue = rowValue;
            this.rowID = rowID;
            this.rowExtraType = rowExtraType;
            this.controlType = controlType;

        }
    }
}
