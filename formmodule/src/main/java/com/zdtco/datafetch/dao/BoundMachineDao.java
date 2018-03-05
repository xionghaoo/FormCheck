package com.zdtco.datafetch.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.zdtco.datafetch.data.BoundMachine;
import com.zdtco.datafetch.data.User;

import java.util.List;

/**
 * Created by G1494458 on 2018/2/27.
 */

@Dao
public interface BoundMachineDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<BoundMachine> boundMachines);

    @Query("SELECT * FROM BoundMachine")
    List<BoundMachine> getAll();
}
