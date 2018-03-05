package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/**
 * Created by G1494458 on 2018/2/27.
 */

@Entity(tableName = "BoundMachine")
public class BoundMachine {
    @NotNull
    @PrimaryKey
    public String machineID;

    public BoundMachine(@NotNull String machineID) {
        this.machineID = machineID;
    }
}
