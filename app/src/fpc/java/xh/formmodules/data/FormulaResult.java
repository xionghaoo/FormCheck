package xh.formmodules.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by G1494458 on 2018/1/12.
 */

public class FormulaResult {

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
        @SerializedName("infocode")
        private String infocode;
        @SerializedName("infoname")
        private String infoname;
        @SerializedName("infotype")
        private String infotype;
        @SerializedName("infotable")
        private String infotable;
        @SerializedName("infokey")
        private String infokey;
        @SerializedName("infovalue")
        private String infovalue;
        @SerializedName("infocon")
        private String infocon;
        @SerializedName("infodatabase")
        private String infodatabase;

        public String getInfocode() {
            return infocode;
        }

        public void setInfocode(String infocode) {
            this.infocode = infocode;
        }

        public String getInfoname() {
            return infoname;
        }

        public void setInfoname(String infoname) {
            this.infoname = infoname;
        }

        public String getInfotype() {
            return infotype;
        }

        public void setInfotype(String infotype) {
            this.infotype = infotype;
        }

        public String getInfotable() {
            return infotable;
        }

        public void setInfotable(String infotable) {
            this.infotable = infotable;
        }

        public String getInfokey() {
            return infokey;
        }

        public void setInfokey(String infokey) {
            this.infokey = infokey;
        }

        public String getInfovalue() {
            return infovalue;
        }

        public void setInfovalue(String infovalue) {
            this.infovalue = infovalue;
        }

        public String getInfocon() {
            return infocon;
        }

        public void setInfocon(String infocon) {
            this.infocon = infocon;
        }

        public String getInfodatabase() {
            return infodatabase;
        }

        public void setInfodatabase(String infodatabase) {
            this.infodatabase = infodatabase;
        }
    }
}
