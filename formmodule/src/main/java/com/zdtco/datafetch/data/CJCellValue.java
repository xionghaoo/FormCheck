package com.zdtco.datafetch.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2018/2/1.
 */

public class CJCellValue {

    public String rowOrder;
    public List<CjItem> cjItems;

    public CJCellValue(String rowOrder) {
        this.rowOrder = rowOrder;
        this.cjItems = new ArrayList<>();
    }

    public static class CjItem {
        public String cellOrder;
        public String cellValue;

        public CjItem(String cellOrder, String cellValue) {
            this.cellOrder = cellOrder;
            this.cellValue = cellValue;
        }
    }
}
