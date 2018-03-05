package xh.formmodules.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by G1494458 on 2018/2/27.
 */

public class UserAndMachineResult {

    @SerializedName("Status")
    public String Status;
    @SerializedName("Message")
    public Object Message;
    @SerializedName("Data")
    public List<Data> Data;

    public static class Data {
        @SerializedName("userid")
        public String userid;
        @SerializedName("DetailedFieldPad")
        public List<DetailedFieldPad> DetailedFieldPad;

        public static class DetailedFieldPad {
            @SerializedName("macno")
            public String macno;
        }
    }
}
