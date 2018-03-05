package com.zdtco.datafetch.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.zdtco.datafetch.data.MultiColumnForm;

import java.util.List;

/**
 * Created by G1494458 on 2017/12/28.
 */

@Dao
public interface MultiColFormDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MultiColumnForm multiColumnForm);

    @Transaction
    @Query("SELECT * FROM MultiColumnForm WHERE formID = :id")
    MultiColumnForm findMultiColFormByID(String id);

}
