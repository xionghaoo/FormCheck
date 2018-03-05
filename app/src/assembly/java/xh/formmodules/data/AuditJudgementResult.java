package xh.formmodules.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by G1494458 on 2018/1/31.
 */

public class AuditJudgementResult {

    @SerializedName("Status")
    public String Status;
    @SerializedName("Message")
    public Object Message;
    @SerializedName("Data")
    public List<Data> Data;

    public static class Data {
        @SerializedName("index")
        public String index;
        @SerializedName("workno")
        public String workno;
        @SerializedName("workname")
        public String workname;
        @SerializedName("judgement")
        public String judgement;
        @SerializedName("auditdatetime")
        public String auditdatetime;
    }
}
