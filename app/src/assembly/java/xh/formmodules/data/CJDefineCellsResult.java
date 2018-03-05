package xh.formmodules.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by G1494458 on 2018/1/31.
 */

public class CJDefineCellsResult {

    @SerializedName("Status")
    public String Status;
    @SerializedName("Message")
    public Object Message;
    @SerializedName("Data")
    public List<Data> Data;

    public static class Data {
        @SerializedName("order")
        public String order;
        @SerializedName("cjItem")
        public List<CjItem> cjItem;

        public static class CjItem {
            @SerializedName("sorderno")
            public String sorderno;
            @SerializedName("svalue")
            public String svalue;
        }
    }
}
