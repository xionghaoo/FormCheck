package com.zdtco.datafetch.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.zdtco.datafetch.data.FormCheckStatus;
import com.zdtco.datafetch.data.FormStub;

import java.util.List;

/**
 * Created by G1494458 on 2018/1/10.
 */

@Dao
public abstract class FormCheckStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertStatus(FormCheckStatus status);

    @Transaction
    @Query("SELECT * FROM FormCheckStatus WHERE machineID = :machineId AND formID = :formId")
    public abstract FormCheckStatus findCheckStatusByID(String machineId, String formId);

    @Delete
    public abstract void deleteCheckStatus(FormCheckStatus status);

}
