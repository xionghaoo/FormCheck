package com.zdtco.datafetch.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * Created by G1494458 on 2017/12/23.
 */

@Entity(tableName = "MachineOwnedForm")
public class MachineOwnedForm implements Parcelable {

    @PrimaryKey
    @NonNull
    public String formID;
    public String refId; //Machine ID
    public String formName;
    public int templateType;
    public long timeLimit;
    public int macType;

    public MachineOwnedForm(@NonNull String formID, String refId, String formName, int templateType, long timeLimit, int macType) {
        this.formID = formID;
        this.refId = refId;
        this.formName = formName;
        this.templateType = templateType;
        this.timeLimit = timeLimit;
        this.macType = macType;
    }

    private MachineOwnedForm(Parcel parcel) {
        formID = parcel.readString();
        refId = parcel.readString();
        formName = parcel.readString();
        templateType = parcel.readInt();
        timeLimit = parcel.readLong();
        macType = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(formID);
        out.writeString(refId);
        out.writeString(formName);
        out.writeInt(templateType);
        out.writeLong(timeLimit);
        out.writeInt(macType);
    }

    public static final Parcelable.Creator<MachineOwnedForm> CREATOR
            = new Parcelable.Creator<MachineOwnedForm>() {
        @Override
        public MachineOwnedForm createFromParcel(Parcel in) {
            return new MachineOwnedForm(in);
        }

        @Override
        public MachineOwnedForm[] newArray(int size) {
            return new MachineOwnedForm[size];
        }
    };

}
