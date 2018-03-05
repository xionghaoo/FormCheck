package xh.formmodules.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by G1494458 on 2017/12/28.
 */

public class CustomCellResult {

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
        @SerializedName("Name")
        private Object Name;
        @SerializedName("Reportcode")
        private String Reportcode;
        @SerializedName("Isxls")
        private Object Isxls;
        @SerializedName("DetailedFieldPad")
        private List<DetailedFieldPad> DetailedFieldPad;

        public Object getName() {
            return Name;
        }

        public void setName(Object Name) {
            this.Name = Name;
        }

        public String getReportcode() {
            return Reportcode;
        }

        public void setReportcode(String Reportcode) {
            this.Reportcode = Reportcode;
        }

        public Object getIsxls() {
            return Isxls;
        }

        public void setIsxls(Object Isxls) {
            this.Isxls = Isxls;
        }

        public List<DetailedFieldPad> getDetailedFieldPad() {
            return DetailedFieldPad;
        }

        public void setDetailedFieldPad(List<DetailedFieldPad> DetailedFieldPad) {
            this.DetailedFieldPad = DetailedFieldPad;
        }

        public static class DetailedFieldPad {
            @SerializedName("reportname")
            private String reportname;
            @SerializedName("orderno")
            private String orderno;
            @SerializedName("itemcode")
            private String itemcode;
            @SerializedName("itemname")
            private String itemname;
            @SerializedName("controltype")
            private String controltype;
            @SerializedName("defaultvlaue")
            private String defaultvlaue;
            @SerializedName("isreadonly")
            private String isreadonly;
            @SerializedName("morevalues")
            private String morevalues;
            @SerializedName("labelwidth")
            private String labelwidth;
            @SerializedName("istop")
            private String istop;
            @SerializedName("getfield")
            private String getfield;
            @SerializedName("setfield")
            private String setfield;
            @SerializedName("modetype")
            private String modetype;

            public String getReportname() {
                return reportname;
            }

            public void setReportname(String reportname) {
                this.reportname = reportname;
            }

            public String getOrderno() {
                return orderno;
            }

            public void setOrderno(String orderno) {
                this.orderno = orderno;
            }

            public String getItemcode() {
                return itemcode;
            }

            public void setItemcode(String itemcode) {
                this.itemcode = itemcode;
            }

            public String getItemname() {
                return itemname;
            }

            public void setItemname(String itemname) {
                this.itemname = itemname;
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

            public String getMorevalues() {
                return morevalues;
            }

            public void setMorevalues(String morevalues) {
                this.morevalues = morevalues;
            }

            public String getLabelwidth() {
                return labelwidth;
            }

            public void setLabelwidth(String labelwidth) {
                this.labelwidth = labelwidth;
            }

            public String getIstop() {
                return istop;
            }

            public void setIstop(String istop) {
                this.istop = istop;
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

            public String getModetype() {
                return modetype;
            }

            public void setModetype(String modetype) {
                this.modetype = modetype;
            }
        }
    }
}
