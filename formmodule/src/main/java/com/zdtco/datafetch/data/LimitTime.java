package com.zdtco.datafetch.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2018/1/25.
 */

public class LimitTime {

    @SerializedName("limittime")
    public List<LTime> lTimeList;

    public LimitTime() {
        this.lTimeList = new ArrayList<>();
    }

    public static class LTime {
        @SerializedName("line")
        public String line;
        @SerializedName("time")
        public int time;

        public LTime(String line, int time) {
            this.line = line;
            this.time = time;
        }
    }
}
