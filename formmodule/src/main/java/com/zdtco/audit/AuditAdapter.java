package com.zdtco.audit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xh.formlib.R;
import com.zdtco.datafetch.data.AuditForm;

import java.util.List;

/**
 * Created by G1494458 on 2018/1/26.
 */

public class AuditAdapter extends ArrayAdapter<AuditForm> {
    public AuditAdapter(@NonNull Context context, List<AuditForm> auditForms) {
        super(context, 0, auditForms);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_audit_form, parent, false);
        }

        AuditForm auditForm = getItem(position);
        TextView tvName = convertView.findViewById(R.id.audit_form_name);
        tvName.setText(auditForm.auditNo);
        return convertView;
    }
}
