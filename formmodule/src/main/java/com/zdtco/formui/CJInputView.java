package com.zdtco.formui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.xh.formlib.R;
import com.zdtco.datafetch.data.CJCellValue;
import com.zdtco.datafetch.data.CJPostItem;
import com.zdtco.datafetch.data.GeneralRow;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Created by G1494458 on 2017/12/29.
 */

public class CJInputView extends LinearLayout {
    public CJInputView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CJInputView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setOrientation(VERTICAL);
    }

    public void initView(List<GeneralRow> generalRows) {
        for (GeneralRow generalRow : generalRows) {
            View layout = LayoutInflater.from(getContext())
                    .inflate(R.layout.widget_cj_row, this, false);
            InputView inputView = layout.findViewById(R.id.input_view);
            inputView.initView(generalRow);
            if (generalRow.rowExtraType == 1) {
                inputView.disableView();
            }
            TextView tvName = layout.findViewById(R.id.item_name);
            tvName.setText(generalRow.rowName);
            addView(layout);
        }
    }

    public String getValue() {
        CJPostItem cjPostItem = new CJPostItem();
        for (int i = 0; i < getChildCount(); i++) {
            InputView inputView = getChildAt(i).findViewById(R.id.input_view);
            String itemCode = (String) inputView.getTag(InputView.Companion.getTAG_ROW_ID_KEY());
            if (!itemCode.equals("LNull")) {
                CJPostItem.CjItem item = new CJPostItem.CjItem(itemCode, inputView.getValue());
                cjPostItem.cjItems.add(item);
            }
        }
        return new Gson().toJson(cjPostItem);
    }

    public void recover(String value) {
        CJPostItem cjPostItem = new Gson().fromJson(value, CJPostItem.class);
        int itemIndex = 0;
        for (int i = 0; i < getChildCount(); i++) {
            InputView inputView = getChildAt(i).findViewById(R.id.input_view);
            String itemCode = (String) inputView.getTag(InputView.Companion.getTAG_ROW_ID_KEY());

            if (itemCode.equals("LNull")) {
                continue;
            }

            inputView.recoverValue(cjPostItem.cjItems.get(itemIndex).itemValue);

            itemIndex ++;
        }
    }

    public void setDefineValue(List<CJCellValue.CjItem> cjItems) {
        for (int i = 0; i < getChildCount(); i++) {
            InputView inputView = getChildAt(i).findViewById(R.id.input_view);
            String orderNo = (String) inputView.getTag(InputView.Companion.getTAG_ROW_ORDER_NO());
            for (CJCellValue.CjItem item : cjItems) {
                if (orderNo.equals(item.cellOrder)) {
                    inputView.recoverValue(item.cellValue);
                }
            }
        }
    }

    public void disableView() {
        for (int i = 0; i < getChildCount(); i++) {
            InputView inputView = getChildAt(i).findViewById(R.id.input_view);
            inputView.disableView();
        }
    }

    public void enabledView() {
        for (int i = 0; i < getChildCount(); i++) {
            InputView inputView = getChildAt(i).findViewById(R.id.input_view);
            inputView.enabledView();
        }
    }

    public String checkInputValidity() {
        for (int i = 0; i < getChildCount(); i++) {
            InputView inputView = getChildAt(i).findViewById(R.id.input_view);
            String itemCode = (String) inputView.getTag(InputView.Companion.getTAG_ROW_ID_KEY());
            if (!itemCode.equals("LNull")) {
                if (!inputView.checkInputValidity().equals("")) {
                    return "输入中含有非法值或空值";
                }
            }
        }
        return "";
    }

}
