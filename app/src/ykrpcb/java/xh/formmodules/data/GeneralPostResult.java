package xh.formmodules.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by G1494458 on 2018/1/3.
 */

public class GeneralPostResult {

    @SerializedName("Status")
    private String Status;
    @SerializedName("Message")
    private String Message;
    @SerializedName("Data")
    private Object Data;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public Object getData() {
        return Data;
    }

    public void setData(Object Data) {
        this.Data = Data;
    }
}
