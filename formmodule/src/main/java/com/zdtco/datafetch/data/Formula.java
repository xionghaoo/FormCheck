package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

/**
 * Created by G1494458 on 2018/1/12.
 */

@Entity(tableName = "Formula")
public class Formula {
    @NotNull
    @PrimaryKey
    public String id;

    public String name;
    public String type;
    public String tableName;
    public String key;
    public String value;
    public String con;
    public String database;

    public Formula(@NotNull String id, String name, String type, String tableName, String key, String value, String con, String database) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.tableName = tableName;
        this.key = key;
        this.value = value;
        this.con = con;
        this.database = database;
    }
}
