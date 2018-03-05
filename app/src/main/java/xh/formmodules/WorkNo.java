package xh.formmodules;

import com.google.gson.annotations.SerializedName;

/**
 * Created by G1494458 on 2017/12/8.
 */

public class WorkNo {

    /**
     * workno : G1494458
     * chnname : 熊皓
     */

    @SerializedName("workno")
    private String workno;
    @SerializedName("chnname")
    private String chnname;

    public String getWorkno() {
        return workno;
    }

    public void setWorkno(String workno) {
        this.workno = workno;
    }

    public String getChnname() {
        return chnname;
    }

    public void setChnname(String chnname) {
        this.chnname = chnname;
    }
}
