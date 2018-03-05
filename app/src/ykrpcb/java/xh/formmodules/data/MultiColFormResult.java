package xh.formmodules.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by G1494458 on 2017/12/23.
 */

public class MultiColFormResult {

    @SerializedName("Status")
    private String Status;
    @SerializedName("Message")
    private Object Message;
    @SerializedName("Data")
    private List<Data> Data;

    private MultiColFormTitleResult titleResult;

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

    public MultiColFormTitleResult getTitleResult() {
        return titleResult;
    }

    public void setTitleResult(MultiColFormTitleResult titleResult) {
        this.titleResult = titleResult;
    }

    public static class Data {
        @SerializedName("Name")
        private String Name;
        @SerializedName("Reportcode")
        private String Reportcode;
        @SerializedName("DetailedFieldPad")
        private List<DetailedFieldPad> DetailedFieldPad;

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getReportcode() {
            return Reportcode;
        }

        public void setReportcode(String Reportcode) {
            this.Reportcode = Reportcode;
        }

        public List<DetailedFieldPad> getDetailedFieldPad() {
            return DetailedFieldPad;
        }

        public void setDetailedFieldPad(List<DetailedFieldPad> DetailedFieldPad) {
            this.DetailedFieldPad = DetailedFieldPad;
        }

        public static class DetailedFieldPad {
            @SerializedName("ctid")
            private String ctid;
            @SerializedName("no")
            private String no;
            @SerializedName("orderno")
            private String orderno;
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
            @SerializedName("frequency")
            private String frequency;
            @SerializedName("controltype")
            private String controltype;
            @SerializedName("defaultvlaue")
            private String defaultvlaue;
            @SerializedName("isreadonly")
            private String isreadonly;
            @SerializedName("getfield")
            private String getfield;
            @SerializedName("setfield")
            private String setfield;
            @SerializedName("morevalues")
            private String morevalues;

            public String getCtid() {
                return ctid;
            }

            public void setCtid(String ctid) {
                this.ctid = ctid;
            }

            public String getNo() {
                return no;
            }

            public void setNo(String no) {
                this.no = no;
            }

            public String getOrderno() {
                return orderno;
            }

            public void setOrderno(String orderno) {
                this.orderno = orderno;
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

            public String getFrequency() {
                return frequency;
            }

            public void setFrequency(String frequency) {
                this.frequency = frequency;
            }

            public String getControltype() {
                return controltype;
            }

            public void setControltype(String controltype) {
                this.controltype = controltype;
            }

            public String getDefaultvlaue() {
                return defaultvlaue;
            }

            public void setDefaultvlaue(String defaultvlaue) {
                this.defaultvlaue = defaultvlaue;
            }

            public String getIsreadonly() {
                return isreadonly;
            }

            public void setIsreadonly(String isreadonly) {
                this.isreadonly = isreadonly;
            }

            public String getGetfield() {
                return getfield;
            }

            public void setGetfield(String getfield) {
                this.getfield = getfield;
            }

            public String getSetfield() {
                return setfield;
            }

            public void setSetfield(String setfield) {
                this.setfield = setfield;
            }

            public String getMorevalues() {
                return morevalues;
            }

            public void setMorevalues(String morevalues) {
                this.morevalues = morevalues;
            }
        }
    }
}
