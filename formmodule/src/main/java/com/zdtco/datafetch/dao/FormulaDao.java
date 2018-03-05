package com.zdtco.datafetch.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.zdtco.datafetch.data.Formula;

import java.util.List;

/**
 * Created by G1494458 on 2018/1/12.
 */

@Dao
public abstract class FormulaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertFormulas(List<Formula> formulas);

    @Query("SELECT * FROM Formula WHERE id = :fID")
    public abstract Formula findFormulaByID(String fID);
}
