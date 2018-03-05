package xh.formmodules.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by G1494458 on 2018/3/1.
 */

public class UserResult {

    @SerializedName("Status")
    public String Status;
    @SerializedName("Message")
    public Object Message;
    @SerializedName("Data")
    public List<Data> Data;

    public static class Data {
        @SerializedName("UserID")
        public String UserID;
        @SerializedName("Name")
        public String Name;
        @SerializedName("AuthList")
        public String AuthList;
    }
}
