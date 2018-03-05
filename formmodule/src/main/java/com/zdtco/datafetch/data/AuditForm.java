package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.Parcelable;

import com.zdtco.datafetch.typeconverter.FormPrintRowConverters;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2018/1/26.
 */

@Entity(tableName = "AuditForm")
public class AuditForm implements Parcelable{

    @PrimaryKey
    @NotNull
    public String auditNo;

    public String machineID;
    public String formID;
    public long printID;
    public String workNo;
    public int formType;

    public AuditForm(@NotNull String auditNo, String machineID, String formID, long printID, String workNo, int formType) {
        this.auditNo = auditNo;
        this.machineID = machineID;
        this.formID = formID;
        this.printID = printID;
        this.workNo = workNo;
        this.formType = formType;
    }

    private AuditForm(Parcel parcel) {
        auditNo = parcel.readString();
        machineID = parcel.readString();
        formID = parcel.readString();
        printID = parcel.readLong();
        workNo = parcel.readString();
        formType = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(auditNo);
        dest.writeString(machineID);
        dest.writeString(formID);
        dest.writeLong(printID);
        dest.writeString(workNo);
        dest.writeInt(formType);
    }

    public static final Parcelable.Creator<AuditForm> CREATOR
            = new Parcelable.Creator<AuditForm>() {
        @Override
        public AuditForm createFromParcel(Parcel source) {
            return new AuditForm(source);
        }

        @Override
        public AuditForm[] newArray(int size) {
            return new AuditForm[size];
        }
    };
}
