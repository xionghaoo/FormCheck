package com.zdtco.datafetch.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.zdtco.datafetch.data.AuditForm;

import java.util.List;

/**
 * Created by G1494458 on 2018/1/26.
 */

@Dao
public interface AuditFormDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(AuditForm auditForm);

    @Transaction
    @Query("SELECT * FROM AuditForm WHERE auditNo = :no")
    AuditForm findAuditFormByID(String no);

    @Transaction
    @Query("SELECT * FROM AuditForm")
    List<AuditForm> loadAll();

    @Delete
    void delete(AuditForm auditForm);
}
