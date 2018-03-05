package com.zdtco.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xh.formlib.R;
import com.zdtco.datafetch.data.GeneralRow;
import com.zdtco.formui.InputView;

/**
 * Created by G1494458 on 2018/1/25.
 */

public class AuditItemView extends GridLayout {
//    public int index;

    public AuditItemView(Context context) {
        super(context);
    }

    public AuditItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void init(Context context, int index) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.widget_audit_item_view, this, false);
        addView(view);
        TextView tvIndex = view.findViewById(R.id.index_txt);
        tvIndex.setText(index + "");

        TextView tvWorkNo = view.findViewById(R.id.work_no_txt);
        tvWorkNo.setText("请扫描工卡");
    }

    public void lastAudit() {
//        TextView tvWorkNo = findViewById(R.id.work_no_txt);
//        EditText tvJudgement = findViewById(R.id.input_txt_view);
//        tvWorkNo.setAlpha(1);
//        tvJudgement.setAlpha(1);
        Button btnAudit = findViewById(R.id.btn_audit);
        btnAudit.setText("核准");
        findViewById(R.id.btn_refuse).setVisibility(GONE);
    }

    public void hasAudit(String workNo, String judgement) {
        TextView tvWorkNo = findViewById(R.id.work_no_txt);
        EditText tvJudgement = findViewById(R.id.input_txt_view);

        tvWorkNo.setText(workNo);
        tvJudgement.setText(judgement);
        tvJudgement.setEnabled(false);

        findViewById(R.id.btn_audit).setEnabled(false);
        findViewById(R.id.btn_refuse).setVisibility(GONE);
    }

    public void todoAudit() {
        TextView tvWorkNo = findViewById(R.id.work_no_txt);
        EditText tvJudgement = findViewById(R.id.input_txt_view);
        tvWorkNo.setAlpha(1);
        tvJudgement.setAlpha(1);
        findViewById(R.id.btn_audit).setAlpha(1);
        findViewById(R.id.btn_refuse).setAlpha(1);
    }

    public void noAudit() {
        TextView tvWorkNo = findViewById(R.id.work_no_txt);
        EditText tvJudgement = findViewById(R.id.input_txt_view);
        tvWorkNo.setAlpha(0);
        tvJudgement.setAlpha(0);
        findViewById(R.id.btn_audit).setAlpha(0);
        findViewById(R.id.btn_refuse).setAlpha(0);
    }

    public void audit() {
        findViewById(R.id.btn_audit).setEnabled(false);
        findViewById(R.id.btn_refuse).setVisibility(GONE);
        EditText tvJudgement = findViewById(R.id.input_txt_view);
        tvJudgement.setEnabled(false);
    }

    public void onAuditClick(OnClickListener listener) {
        findViewById(R.id.btn_audit).setOnClickListener(listener);
    }

    public void onRefuseClick(OnClickListener listener) {
        findViewById(R.id.btn_refuse).setOnClickListener(listener);
    }

    public void setWorkNo(String workNo) {
        TextView tvWorkNo = findViewById(R.id.work_no_txt);
        tvWorkNo.setText(workNo);
    }

    public void showIndexCircle(boolean show) {
        if (show) {
            findViewById(R.id.index_circle).setVisibility(VISIBLE);
        } else {
            findViewById(R.id.index_circle).setVisibility(GONE);
        }
    }

    public void setOnIndexClickListener(OnClickListener listener) {
        findViewById(R.id.index_txt).setOnClickListener(listener);
    }

    public String getJudgement() {
        EditText editText = findViewById(R.id.input_txt_view);
        return editText.getText().toString().trim();
    }

    public String getWorkNo() {
        TextView tvWorkNo = findViewById(R.id.work_no_txt);
        return tvWorkNo.getText().toString().trim();
    }
}
