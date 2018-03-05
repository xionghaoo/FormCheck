package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;

import org.jetbrains.annotations.NotNull;

/**
 * Created by G1494458 on 2018/2/26.
 */

@Entity(tableName = "MachineAndForm", primaryKeys = {"machineID", "formID"})
public class MachineAndForm {
    @NotNull
    public String machineID;
    @NotNull
    public String formID;

    public MachineAndForm(@NotNull String machineID, @NotNull String formID) {
        this.machineID = machineID;
        this.formID = formID;
    }
}
