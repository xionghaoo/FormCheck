package com.zdtco.datafetch.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.zdtco.datafetch.data.MachineAndForm;

import java.util.List;

/**
 * Created by G1494458 on 2018/2/26.
 */
@Dao
public interface MachineAndFormDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<MachineAndForm> machineAndForms);

    @Query("SELECT A.machineID FROM (SELECT DISTINCT * From MachineAndForm WHERE formID = :formID) A JOIN (SELECT * FROM BoundMachine) B ON A.machineID = B.machineID")
    List<String> loadMachinesByForm(String formID);
}
