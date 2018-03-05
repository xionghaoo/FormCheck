package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.zdtco.datafetch.typeconverter.MachineFormTypeConverters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2017/12/8.
 */

@Entity(tableName = "Machine")
public class Machine {

    @PrimaryKey
    @NonNull
    public String id;
    public String machineName;

//    @Relation(parentColumn = "id", entityColumn = "refId", entity = MachineOwnedForm.class)
    @TypeConverters(MachineFormTypeConverters.class)
    public List<MachineOwnedForm> machineOwnedForms;

    public String facID;
    public String facName;
    public String frID;
    public String frName;
    public String buID;
    public String buName;
    public String fac2ID;
    public String fac2Name;
    public String fac2DeptID;
    public String fac2DeptName;

    //线体
    public String line;
    public String lineName;
    //课别
    public String classr;

    public String nfcCode;

    public String limitTime;

    public String isStub;

    @Ignore
    public List<MachineAndForm> machineAndForms;

    public Machine(@NonNull String id) {
        this.id = id;
        machineOwnedForms = new ArrayList<>();
        machineAndForms = new ArrayList<>();
    }
}
