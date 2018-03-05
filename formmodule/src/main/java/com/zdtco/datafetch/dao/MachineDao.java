package com.zdtco.datafetch.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.zdtco.datafetch.data.Line;
import com.zdtco.datafetch.data.Machine;
import com.zdtco.datafetch.data.MachineOwnedForm;

import java.util.List;

/**
 * Created by G1494458 on 2017/12/8.
 */

@Dao
public abstract class MachineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(Machine machine);

    @Transaction
    @Query("SELECT * FROM Machine WHERE nfcCode = :code OR nfcCode = :upperCode")
    public abstract List<Machine> findMachinesByNFCCode(String code, String upperCode);

    @Transaction
    @Query("SELECT * FROM Machine WHERE line = :line")
    public abstract List<Machine> findMachineByLine(String line);

    @Transaction
    @Query("SELECT * FROM Machine WHERE id = :code")
    public abstract Machine findMachineByID(String code);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateMachineRecord(Machine machine);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateMachineRecords(List<Machine> machines);

    @Transaction
    @Query("SELECT * FROM Machine WHERE id = :id")
    public abstract List<Machine> findMachinesByID(String id);

    @Transaction
    @Query("SELECT id, machineName, facName, line, classr, nfcCode, limitTime FROM Machine")
    public abstract List<Machine> findAllMachines();

    @Transaction
    @Query("SELECT DISTINCT line, lineName, classr, limitTime FROM Machine")
    public abstract List<Line> findAllLines();

    //OR machineName LIKE :query OR line LIKE :query OR classr LIKE :query
    @Transaction
    @Query("SELECT id, machineName, facName, line, classr, nfcCode FROM Machine WHERE id LIKE :query OR machineName LIKE :query OR line LIKE :query OR classr LIKE :query")
    public abstract List<Machine> findMachinesByQuery(String query);

    @Transaction
    public void updateTimeLimit(String id, long timeLimit) {
        List<Machine> machines = findMachineByLine(id);
        for (Machine m : machines) {
            m.limitTime = timeLimit + "";
            for (MachineOwnedForm ownedForm : m.machineOwnedForms) {
                ownedForm.timeLimit = timeLimit;
            }
        }
        updateMachineRecords(machines);
    }
}
