package com.zdtco.formui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.xh.formlib.R;
import com.zdtco.datafetch.data.SelectBoundMachineItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by G1494458 on 2018/2/27.
 */

public class BoundMachineAdapter extends ArrayAdapter<SelectBoundMachineItem> {

    private List<SelectBoundMachineItem> selectBoundMachineItems;

    public BoundMachineAdapter(@NonNull Context context, List<SelectBoundMachineItem> list) {
        super(context, 0);
        selectBoundMachineItems = list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.dialog_select_list_item, parent, false);
        }
        final CheckBox checkBox = convertView.findViewById(R.id.check_box);

        final SelectBoundMachineItem item = selectBoundMachineItems.get(position);

        if (item != null) {
            checkBox.setText(item.machineID);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    item.checked = isChecked;
                }
            });
            checkBox.setChecked(item.checked);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return selectBoundMachineItems == null ? 0 : selectBoundMachineItems.size();
    }

    public List<String> getMachineIDList() {
        List<String> list = new ArrayList<>();
        for (SelectBoundMachineItem item : selectBoundMachineItems) {
            if (item.checked) {
                list.add(item.machineID);
            }
        }
        return list;
    }
}
