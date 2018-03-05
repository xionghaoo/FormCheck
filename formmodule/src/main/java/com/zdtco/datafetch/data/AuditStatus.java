package com.zdtco.datafetch.data;

/**
 * Created by G1494458 on 2018/1/26.
 */

public class AuditStatus {
    public int index;
    public boolean hasAudit;
    public String userWorkNo;
    public String judgement;

    public AuditStatus(int index, boolean hasAudit, String userWorkNo, String judgement) {
        this.index = index;
        this.hasAudit = hasAudit;
        this.userWorkNo = userWorkNo;
        this.judgement = judgement;
    }
}
