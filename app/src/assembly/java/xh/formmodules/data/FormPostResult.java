package xh.formmodules.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by G1494458 on 2018/1/31.
 */

public class FormPostResult {

    @SerializedName("Status")
    public String Status;
    @SerializedName("Message")
    public String Message;
    @SerializedName("Data")
    public String Data;

    public FormPostResult(String status, String message, String data) {
        Status = status;
        Message = message;
        Data = data;
    }
}
