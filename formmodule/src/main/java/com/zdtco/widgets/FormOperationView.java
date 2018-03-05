package com.zdtco.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xh.formlib.R;

/**
 * Created by G1494458 on 2018/1/4.
 */

public class FormOperationView extends FrameLayout {

    public FormOperationView(Context context) {
        super(context);
        init(context);
    }

    public FormOperationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.widget_form_operation, this, false);
        addView(v);
    }

    public void showFormDescription(String formID, String description) {
        ViewStub viewStub = getChildAt(0).findViewById(R.id.view_stub);
        viewStub.setLayoutResource(R.layout.form_description);
        View v = viewStub.inflate();

        TextView tvNumber = v.findViewById(R.id.form_number);
        TextView tvDesc = v.findViewById(R.id.form_desc);
        tvNumber.setText("表单编号: " + formID);
        tvDesc.setText("说明: " + description);
    }

    public void setOnPostListener(OnClickListener listener) {
        ((Button) findViewById(R.id.post_input_data)).setOnClickListener(listener);
    }

    public void setOnAuditListener(OnClickListener listener) {
        ((Button) findViewById(R.id.audit)).setOnClickListener(listener);
    }

    public void showAuditButton() {
        ((Button) findViewById(R.id.audit)).setVisibility(VISIBLE);
    }

    public void hidePostButton() {
        ((Button) findViewById(R.id.post_input_data)).setVisibility(GONE);
    }

    public void disableView() {
        ((Button) findViewById(R.id.post_input_data)).setEnabled(false);
        ((Button) findViewById(R.id.audit)).setEnabled(false);
    }

    public void enabledView() {
        ((Button) findViewById(R.id.post_input_data)).setEnabled(true);
        ((Button) findViewById(R.id.audit)).setEnabled(true);
    }

    public void enableAuditButton(boolean enable) {
        if (enable) {
            ((Button) findViewById(R.id.audit)).setEnabled(true);
        } else {
            ((Button) findViewById(R.id.audit)).setEnabled(false);
        }
    }

}
