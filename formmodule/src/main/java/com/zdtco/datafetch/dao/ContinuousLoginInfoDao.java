package com.zdtco.datafetch.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.zdtco.datafetch.data.ContinuousLoginInfo;

/**
 * Created by G1494458 on 2018/2/3.
 */

@Dao
public interface ContinuousLoginInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ContinuousLoginInfo info);

    @Query("SELECT * FROM ContinuousLoginInfo WHERE workNo = :workNo")
    ContinuousLoginInfo findInfoByID(String workNo);
}
