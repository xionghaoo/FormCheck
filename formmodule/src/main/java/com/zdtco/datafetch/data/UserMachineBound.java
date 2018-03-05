package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.zdtco.datafetch.typeconverter.BoundMachineTypeConverters;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2018/2/27.
 */

@Entity(tableName = "UserMachineBound")
public class UserMachineBound {
    @NotNull
    @PrimaryKey
    public String userWorkNo;
    @TypeConverters(BoundMachineTypeConverters.class)
    public List<BoundMachine> boundMachines;

    public UserMachineBound(@NotNull String userWorkNo) {
        this.userWorkNo = userWorkNo;
        boundMachines = new ArrayList<>();
    }
}
