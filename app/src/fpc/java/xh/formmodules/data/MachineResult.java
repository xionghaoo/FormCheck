package xh.formmodules.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by G1494458 on 2017/12/23.
 */

public class MachineResult {

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
        @SerializedName("Equ_id")
        private String EquId;
        @SerializedName("Equ_name")
        private String EquName;
        @SerializedName("Nfctag")
        private String Nfctag;
        @SerializedName("factory")
        private String factory;
        @SerializedName("classr")
        private String classr;
        @SerializedName("line")
        private String line;
        @SerializedName("linename")
        private String linename;
        @SerializedName("limittime")
        private String limittime;
        @SerializedName("ownedForms")
        private List<OwnedForms> ownedForms;

        public String getEquId() {
            return EquId;
        }

        public void setEquId(String EquId) {
            this.EquId = EquId;
        }

        public String getEquName() {
            return EquName;
        }

        public void setEquName(String EquName) {
            this.EquName = EquName;
        }

        public String getNfctag() {
            return Nfctag;
        }

        public void setNfctag(String Nfctag) {
            this.Nfctag = Nfctag;
        }

        public String getFactory() {
            return factory;
        }

        public void setFactory(String factory) {
            this.factory = factory;
        }

        public String getClassr() {
            return classr;
        }

        public void setClassr(String classr) {
            this.classr = classr;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String getLinename() {
            return linename;
        }

        public void setLinename(String linename) {
            this.linename = linename;
        }

        public String getLimittime() {
            return limittime;
        }

        public void setLimittime(String limittime) {
            this.limittime = limittime;
        }

        public List<OwnedForms> getOwnedForms() {
            return ownedForms;
        }

        public void setOwnedForms(List<OwnedForms> ownedForms) {
            this.ownedForms = ownedForms;
        }

        public static class OwnedForms {
            @SerializedName("reportcode")
            private String reportcode;
            @SerializedName("reportname")
            private String reportname;
            @SerializedName("form")
            private String form;
            @SerializedName("mactype")
            private String mactype;

            public String getReportcode() {
                return reportcode;
            }

            public void setReportcode(String reportcode) {
                this.reportcode = reportcode;
            }

            public String getReportname() {
                return reportname;
            }

            public void setReportname(String reportname) {
                this.reportname = reportname;
            }

            public String getForm() {
                return form;
            }

            public void setForm(String form) {
                this.form = form;
            }

            public String getMactype() {
                return mactype;
            }

            public void setMactype(String mactype) {
                this.mactype = mactype;
            }
        }
    }
}
