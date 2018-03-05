package com.zdtco.datafetch.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.zdtco.datafetch.data.GeneralForm;
import com.zdtco.datafetch.data.Machine;

import java.util.List;

/**
 * Created by G1494458 on 2017/12/23.
 */
@Dao
public interface GeneralFormDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(GeneralForm machine);

    @Transaction
    @Query("SELECT * FROM GeneralForm WHERE id = :formID")
    GeneralForm findFormByID(String formID);
}
