package com.zdtco.datafetch.data;

/**
 * Created by G1494458 on 2018/1/8.
 */

public class FormPrintRow {
    public String rowName;
    public String rowValue;
    public String rowID;
    public String extraID;

    public FormPrintRow(String rowName, String rowValue, String rowID, String extraID) {
        this.rowName = rowName;
        this.rowValue = rowValue;
        this.rowID = rowID;
        this.extraID = extraID;
    }
}
