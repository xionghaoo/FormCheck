package com.zdtco.datafetch.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2018/1/23.
 */

public class TemplateFiveFormPostData {

    /**
     * class : 白班
     * Comment :
     * EndTime : 1970/01/17 06:31:08
     * Equ_id : KB002#
     * Equ_name : 空板2線
     * FieldItemsHeader : [{"istop":"1","itemcode":"ITEM1","key":"開始時間","value":"1970/01/16 14:59:49"},{"istop":"1","itemcode":"ITEM1","key":"批號","value":"jfjhg"},{"istop":"1","itemcode":"ITEM1","key":"料號","value":"uyy"}]
     * FieldItemsContent : [{"istop":"2","itemcode":"ITEM2","key":"結束時間","value":"1970/01/17 06:31:08","no":1},{"istop":"2","itemcode":"ITEM5","key":"記錄人","value":"wyg","no":1},{"istop":"2","itemcode":"ITEM4","key":"責任線長","value":"dhdbnd","no":1},{"istop":"2","itemcode":"ITEM3","key":"責任人","value":"djnxnx","no":1},{"istop":"2","itemcode":"ITEM2","key":"不良數","value":"dhendn","no":1},{"istop":"2","itemcode":"ITEM1","key":"不良項目","value":"djjdn","no":1},{"istop":"2","itemcode":"ITEM2","key":"結束時間","value":"1970/01/17 06:31:08","no":2},{"istop":"2","itemcode":"ITEM5","key":"記錄人","value":"wyg","no":2},{"istop":"2","itemcode":"ITEM4","key":"責任線長","value":"dhdbnd","no":2},{"istop":"2","itemcode":"ITEM3","key":"責任人","value":"djnxnx","no":2},{"istop":"2","itemcode":"ITEM2","key":"不良數","value":"dhendn","no":2},{"istop":"2","itemcode":"ITEM1","key":"不良項目","value":"djjdn","no":2}]
     * modetype : 5
     * reportcode : SfceWebQT232
     * reportname : 空板抽檢報表
     * StartTime : 1970/01/17 06:30:23
     * WorkNo : G1494458
     */

    @SerializedName("WorkNo")
    public String workNo;
    @SerializedName("Equ_id")
    public String machineID;
    @SerializedName("reportcode")
    public String formID;
    @SerializedName("modetype")
    public String formType;
    @SerializedName("class")
    public String clsr;
    @SerializedName("Comment")
    public String comment;
    @SerializedName("StartTime")
    public String startTime;
    @SerializedName("EndTime")
    public String endTime;
//    @SerializedName("Equ_name")
//    private String EquName;
//    @SerializedName("reportname")
//    private String reportname;
    @SerializedName("Mod5No")
    public String recordsNum;
    @SerializedName("FieldItemsHeader")
    public List<FormHeader> header = new ArrayList<>();
    @SerializedName("FieldItemsContent")
    public List<FormContent> content = new ArrayList<>();

    public TemplateFiveFormPostData(String workNo, String machineID, String formID, String formType, String clsr, String comment, String startTime, String endTime, String recordsNum) {
        this.workNo = workNo;
        this.machineID = machineID;
        this.formID = formID;
        this.formType = formType;
        this.clsr = clsr;
        this.comment = comment;
        this.startTime = startTime;
        this.endTime = endTime;
        this.recordsNum = recordsNum;
    }

    public static class FormHeader {
        /**
         * istop : 1
         * itemcode : ITEM1
         * key : 開始時間
         * value : 1970/01/16 14:59:49
         */

        @SerializedName("istop")
        public String istop;
        @SerializedName("itemcode")
        public String itemcode;
        @SerializedName("key")
        public String key;
        @SerializedName("value")
        public String value;
        @SerializedName("orderno")
        public String orderno;

        public FormHeader(String istop, String itemcode, String key, String value, String orderno) {
            this.istop = istop;
            this.itemcode = itemcode;
            this.key = key;
            this.value = value;
            this.orderno = orderno;
        }
    }

    public static class FormContent {
        /**
         * istop : 2
         * itemcode : ITEM2
         * key : 結束時間
         * value : 1970/01/17 06:31:08
         * no : 1
         */

        @SerializedName("istop")
        public String istop;
        @SerializedName("itemcode")
        public String itemcode;
        @SerializedName("key")
        public String key;
        @SerializedName("value")
        public String value;
        @SerializedName("no")
        public int no;
        @SerializedName("orderno")
        public String orderno;

        public FormContent(String istop, String itemcode, String key, String value, int no, String orderno) {
            this.istop = istop;
            this.itemcode = itemcode;
            this.key = key;
            this.value = value;
            this.no = no;
            this.orderno = orderno;
        }
    }
}
