package com.zdtco.datafetch.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2017/12/23.
 */

public class MultiColFormRow {
    public String titleDesc;
    public String title;

    public InputRow inputRow;
    public List<MultiColDisplayCell> colDisplay;

    public MultiColFormRow() {
        colDisplay = new ArrayList<>();
    }

    public static class InputRow {
        public String rowID;

        public String rowName;

        public String indexA;   //orderNo

        public String defaultValue;

        public int inputType;

        public boolean isMustWrite;

        public String initValue;

        public String formulaExecuteRow;
        public String formulaParameterRows;

        public InputRow(String rowID, String rowName, String indexA, String defaultValue,
                        int inputType, boolean isMustWrite, String initValue,
                        String executeRow, String parameterRows) {
            this.rowID = rowID;
            this.rowName = rowName;
            this.indexA = indexA;
            this.defaultValue = defaultValue;
            this.inputType = inputType;
            this.isMustWrite = isMustWrite;
            this.initValue = initValue;
            formulaExecuteRow = executeRow;
            formulaParameterRows = parameterRows;
        }
    }
}
