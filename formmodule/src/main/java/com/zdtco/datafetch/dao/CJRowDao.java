package com.zdtco.datafetch.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.zdtco.datafetch.data.CJRow;
import com.zdtco.datafetch.data.GeneralForm;

/**
 * Created by G1494458 on 2018/1/2.
 */

@Dao
public interface CJRowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CJRow cjRow);

    @Transaction
    @Query("SELECT * FROM CJRow WHERE id = :rowID")
    CJRow findCJRowByID(String rowID);
}
