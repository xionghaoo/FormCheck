package com.zdtco.datafetch.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.zdtco.datafetch.data.FormPrintData;

import java.util.List;

/**
 * Created by G1494458 on 2018/1/8.
 */

@Dao
public abstract class FormPrintDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Long insert(FormPrintData formStub);

    @Transaction
    @Query("SELECT * FROM FormPrintData WHERE machineID = :machineId AND formID = :formId AND id = :printID")
    public abstract FormPrintData findFormPrintDataByID(String machineId, String formId, long printID);

    @Transaction
    @Query("SELECT * FROM FormPrintData WHERE hasPost = :hasPost")
    public abstract List<FormPrintData> loadAll(boolean hasPost);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void updateFormPrintRecord(FormPrintData printData);

    @Delete
    public abstract void deleteFormPrintData(FormPrintData formStub);

//    @Transaction
//    public void updateTemporaryRecord(FormPrintData printData) {
//        FormPrintData tmp = findFormPrintDataByID(printData.machineID, printData.formID);
//        if (tmp == null) {
//            insert(printData);
//        } else {
//            updateFormPrintRecord(printData);
//        }
//    }
}
