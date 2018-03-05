package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2017/12/23.
 */

//@Entity(tableName = "GeneralRow")
public class GeneralRow {

//    @NonNull
//    @PrimaryKey
    public String rowID;

    public String rowName;

    public String indexA;   //orderNo

    public String indexB;   //itemCode

    public String rowNameExtra;

    public String defaultValue;

    //初件表集合栏位ID
    public String cjRowID;

    public int inputType;

    public boolean isMustWrite;  //isreadonly

    public String initValue;

    public int rowExtraType;   //istop

    public String formulaExecuteRow;
    public String formulaParameterRows;

//    @Ignore
    public List<GeneralRow> cjRowDatas;

    public GeneralRow(@NonNull String rowID, String rowName, String indexA, String indexB, String defaultValue, String cjRowID,
                      int inputType, boolean isMustWrite, String initValue, String nameExtra, int rowExtraType, String excuteRow, String parameterRows) {
        this.rowID = rowID;
        this.rowName = rowName;
        this.indexA = indexA;
        this.indexB = indexB;
        this.defaultValue = defaultValue;
        this.cjRowID = cjRowID;
        this.inputType = inputType;
        this.isMustWrite = isMustWrite;
        this.initValue = initValue;
        this.rowNameExtra = nameExtra;
        this.rowExtraType = rowExtraType;
        formulaExecuteRow = excuteRow;
        formulaParameterRows = parameterRows;
        cjRowDatas = new ArrayList<>();
    }
}
