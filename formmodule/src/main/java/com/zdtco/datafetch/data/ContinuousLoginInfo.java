package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Created by G1494458 on 2018/2/3.
 */

@Entity(tableName = "ContinuousLoginInfo")
public class ContinuousLoginInfo {

    @NotNull
    @PrimaryKey
    public String workNo;
    public int continuousDays;
    public long date;

    public ContinuousLoginInfo(@NotNull String workNo, int continuousDays, long date) {
        this.workNo = workNo;
        this.continuousDays = continuousDays;
        this.date = date;
    }
}
