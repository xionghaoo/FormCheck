package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;

import org.jetbrains.annotations.NotNull;

/**
 * Created by G1494458 on 2018/1/11.
 */

@Entity(tableName = "FormCheckStatus", primaryKeys = {"machineID", "formID"})
public class FormCheckStatus {
    @NotNull
    public String machineID;
    @NotNull
    public String formID;
    public boolean isCheckedFirstForm;

    public FormCheckStatus(@NotNull String machineID, @NotNull String formID) {
        this.machineID = machineID;
        this.formID = formID;
    }
}
