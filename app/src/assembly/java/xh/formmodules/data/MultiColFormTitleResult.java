package xh.formmodules.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by G1494458 on 2017/12/28.
 */

public class MultiColFormTitleResult {

    @SerializedName("Status")
    private String Status;
    @SerializedName("Message")
    private Object Message;
    @SerializedName("Data")
    private List<Data> Data;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public Object getMessage() {
        return Message;
    }

    public void setMessage(Object Message) {
        this.Message = Message;
    }

    public List<Data> getData() {
        return Data;
    }

    public void setData(List<Data> Data) {
        this.Data = Data;
    }

    public static class Data {
        @SerializedName("Reportcode")
        private String Reportcode;
        @SerializedName("field1")
        private String field1;
        @SerializedName("field2")
        private String field2;
        @SerializedName("field3")
        private String field3;
        @SerializedName("field4")
        private String field4;
        @SerializedName("field5")
        private String field5;
        @SerializedName("field6")
        private String field6;
        @SerializedName("field7")
        private String field7;

        public String getReportcode() {
            return Reportcode;
        }

        public void setReportcode(String Reportcode) {
            this.Reportcode = Reportcode;
        }

        public String getField1() {
            return field1;
        }

        public void setField1(String field1) {
            this.field1 = field1;
        }

        public String getField2() {
            return field2;
        }

        public void setField2(String field2) {
            this.field2 = field2;
        }

        public String getField3() {
            return field3;
        }

        public void setField3(String field3) {
            this.field3 = field3;
        }

        public String getField4() {
            return field4;
        }

        public void setField4(String field4) {
            this.field4 = field4;
        }

        public String getField5() {
            return field5;
        }

        public void setField5(String field5) {
            this.field5 = field5;
        }

        public String getField6() {
            return field6;
        }

        public void setField6(String field6) {
            this.field6 = field6;
        }

        public String getField7() {
            return field7;
        }

        public void setField7(String field7) {
            this.field7 = field7;
        }
    }
}
