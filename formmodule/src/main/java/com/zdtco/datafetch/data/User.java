package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by G1494458 on 2017/12/16.
 */

@Entity(tableName = "User")
public class User {

    @NonNull
    @PrimaryKey
    public String workNo;
    public String userName;
    public String groupID;
    public String userAuths;

    public User(@NonNull String workNo, String userName, String groupID, String userAuths) {
        this.workNo = workNo;
        this.userName = userName;
        this.groupID = groupID;
        this.userAuths = userAuths;
    }
}
