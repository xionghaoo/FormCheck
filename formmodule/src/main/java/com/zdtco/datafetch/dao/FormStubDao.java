package com.zdtco.datafetch.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.zdtco.datafetch.data.FormPrintData;
import com.zdtco.datafetch.data.FormStub;

import java.util.List;

/**
 * Created by G1494458 on 2018/1/10.
 */

@Dao
public abstract class FormStubDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertFormStub(FormStub formStub);

    @Transaction
    @Query("SELECT * FROM FormStub WHERE machineID = :machineId AND formID = :formId AND mergeIndex = :index")
    public abstract FormStub findFormStubByID(String machineId, String formId, int index);

    @Transaction
    @Query("SELECT * FROM FormStub")
    public abstract List<FormStub> loadAll();

    @Delete
    public abstract void deleteFormStub(FormStub formStub);

//    @Transaction
//    public void updateTemporaryRecord(FormPrintData printData) {
//        FormPrintData tmp = findFormStubByID(printData.machineID, printData.formID);
//        if (tmp == null) {
//            insert(printData);
//        } else {
//            findFormStubByID(printData);
//        }
//    }
}
