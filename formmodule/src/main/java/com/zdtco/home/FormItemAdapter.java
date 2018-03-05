package com.zdtco.home;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.xh.formlib.R;
import com.zdtco.datafetch.data.MachineOwnedForm;
import com.zdtco.datafetch.data.MultiColDisplayCell;

import java.util.List;

/**
 * Created by G1494458 on 2017/11/28.
 */

public class FormItemAdapter extends ArrayAdapter<MachineOwnedForm> {

    public FormItemAdapter(@NonNull Context context, List<MachineOwnedForm> cols) {
        super(context, 0, cols);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_machine_list_form, parent, false);
        }
        MachineOwnedForm item = getItem(position);
        TextView tv = convertView.findViewById(R.id.form_name);
        tv.setText(item.formName);
        return convertView;
    }

}
