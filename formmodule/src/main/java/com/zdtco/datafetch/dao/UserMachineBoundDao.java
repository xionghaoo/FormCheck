package com.zdtco.datafetch.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.zdtco.datafetch.data.UserMachineBound;

/**
 * Created by G1494458 on 2018/2/27.
 */

@Dao
public interface UserMachineBoundDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserMachineBound userMachineBound);

    @Transaction
    @Query("SELECT * FROM UserMachineBound WHERE userWorkNo = :workNo")
    UserMachineBound findUserMachineBoundByID(String workNo);
}
