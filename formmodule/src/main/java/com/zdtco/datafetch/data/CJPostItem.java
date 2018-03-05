package com.zdtco.datafetch.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2018/1/23.
 */

public class CJPostItem {
    @SerializedName("cjItems")
    public List<CjItem> cjItems;

    public CJPostItem() {
        this.cjItems = new ArrayList<>();
    }

    public static class CjItem {
        @SerializedName("itemCode")
        public String itemCode;
        @SerializedName("itemValue")
        public String itemValue;

        public CjItem(String itemCode, String itemValue) {
            this.itemCode = itemCode;
            this.itemValue = itemValue;
        }
    }
}
