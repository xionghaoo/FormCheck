package com.zdtco.datafetch.data;

import com.google.gson.annotations.SerializedName;

/**
 * Created by G1494458 on 2018/1/31.
 */

public class FormPaperNo {

    //表单提交之后生成的单号
    public String paperNo;
    //提交结果
    public boolean isSuccess;

    public FormPaperNo(String paperno, boolean isSuccess) {
        this.paperNo = paperno;
        this.isSuccess = isSuccess;
    }
}
