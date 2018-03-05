package com.zdtco.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.xh.formlib.R;
import com.zdtco.FUtil;
import com.zdtco.datafetch.data.FormPrintData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by G1494458 on 2017/02/21.
 */

public class PostFormListAdapter extends BaseExpandableListAdapter {

    protected SimpleDateFormat dateParser = new SimpleDateFormat("yyyy/MM/dd", Locale.CHINA);
    protected SimpleDateFormat dateFormatShow = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);
    protected SimpleDateFormat dateFormatSort = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA);

    private List<String> headers = new ArrayList<>();

    private HashMap<String, List<FormPrintData>> itemsMap = new HashMap<>();

    private List<FormPrintData> mItems;

    public PostFormListAdapter(List<FormPrintData> mItems) {
//        this.context = context;
        this.mItems = mItems;
    }

    public void notifyDataStructureChanged(List<FormPrintData> savedFieldItems) {
        mItems.clear();

        for (FormPrintData printData : savedFieldItems) {
            if (!printData.hasDeleted) {
                mItems.add(printData);
            }
        }

        headers.clear();
        itemsMap.clear();

        //按时间倒序
        Collections.sort(mItems, new Comparator<FormPrintData>() {
            @Override
            public int compare(FormPrintData o1, FormPrintData o2) {
                int status = 0;
                try {
                    Date time1 = dateFormatSort.parse(o1.endTime);
                    Date time2 = dateFormatSort.parse(o2.endTime);
                    if (time1.after(time2)) {
                        status = -1;
                    } else if (time1.before(time2)){
                        status = 1;
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return status;
            }
        });

        for (FormPrintData item : mItems) {
            try {
                Date parseResult = dateParser.parse(item.endTime);
                final String date = dateFormatShow.format(parseResult);
                if (itemsMap.get(date) == null) {
                    itemsMap.put(date, new ArrayList<FormPrintData>());
                    headers.add(date);
                }
                itemsMap.get(date).add(item);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }

    public void updateItems(List<FormPrintData> items, boolean hasPost) {

    }

    @Override
    public int getGroupCount() {
        return headers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return itemsMap.get(headers.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return headers.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return itemsMap.get(headers.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String item = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.item_group_saved_list, parent, false);
            //默认展开Group
            ExpandableListView expandableListView = (ExpandableListView) parent;
            expandableListView.expandGroup(groupPosition);
            isExpanded = true;
        }
        TextView tv = (TextView) convertView.findViewById(R.id.time);
        tv.setText(item);
        ImageView iconExpand = (ImageView) convertView.findViewById(R.id.icon_expand);
        if (isExpanded) {
            iconExpand.animate()
                    .scaleY(1f)
                    .setDuration(200)
                    .start();
        } else {
            iconExpand.animate()
                    .scaleY(-1f)
                    .setDuration(200)
                    .start();
        }
        TextView tvCount = (TextView) convertView.findViewById(R.id.count);
        tvCount.setText("共 " + getChildrenCount(groupPosition) + " 条记录");
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final FormPrintData item = (FormPrintData) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.item_saved_field_list, parent, false);
        }
        TextView tableName = (TextView) convertView.findViewById(R.id.table_name);
        tableName.setText("点检表名: "+item.formID);
        TextView equName1 = (TextView) convertView.findViewById(R.id.equ_name_text);
        equName1.setText("机台名称: "+item.machineID);
        TextView equName2 = (TextView) convertView.findViewById(R.id.equ_name_2_text);
        equName2.setText("机台编号: "+item.machineID);
        TextView recordTime = (TextView) convertView.findViewById(R.id.record_time);
        recordTime.setText("记录时间: "+item.endTime);
        CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.check_box);
        checkBox.setChecked(item.hasChecked);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                item.hasChecked = b;
            }
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public List<FormPrintData> getCheckedItems() {
        List<FormPrintData> checkedItems = new ArrayList<>();
        for (FormPrintData data : mItems) {
            if (data.hasChecked) {
                checkedItems.add(data);
            }
        }
        return checkedItems;
    }

    public void selectAll() {
        for (FormPrintData data : mItems) {
            data.hasChecked = true;
        }
        notifyDataSetChanged();
    }

    public void releaseAll() {
        for (FormPrintData data : mItems) {
            data.hasChecked = false;
        }
        notifyDataSetChanged();
    }

}
