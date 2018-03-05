package com.zdtco.datafetch.data;

import android.support.annotation.NonNull;

/**
 * Created by G1494458 on 2017/12/23.
 */

public class MultiColDisplayCell implements Comparable<MultiColDisplayCell> {
    public String dispCellName;
    public String dispCellValue;

    public MultiColDisplayCell() {
    }

    public MultiColDisplayCell(String dispCellName, String dispCellValue) {
        this.dispCellName = dispCellName;
        this.dispCellValue = dispCellValue;
    }

    @Override
    public int compareTo(@NonNull MultiColDisplayCell o) {
        return o.dispCellValue.length() - dispCellValue.length();
    }
}
