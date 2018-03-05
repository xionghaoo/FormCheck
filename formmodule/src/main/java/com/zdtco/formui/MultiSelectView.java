package com.zdtco.formui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.xh.formlib.R;

import java.util.List;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * Created by G1494458 on 2017/12/29.
 */

public class MultiSelectView extends HorizontalScrollView {

    private List<String> mSelectItems;
    private FrameLayout frameLayout;

    public MultiSelectView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public MultiSelectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        frameLayout = new FrameLayout(context);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
        addView(frameLayout);
    }

    public void setSelectItems(List<String> selectItems) {
        if (selectItems == null) {
            return;
        }
        this.mSelectItems = selectItems;

        final int itemNum = selectItems.size();
        RadioGroup radioGroup = new RadioGroup(getContext());
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);

//        if (itemNum > 3) {
//            radioGroup.setOrientation(LinearLayout.VERTICAL);
//        } else {
//            radioGroup.setOrientation(LinearLayout.HORIZONTAL);
//        }

        for (int i = 0; i < itemNum; i++) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(selectItems.get(i));
            radioButton.setId(i + 600);
            radioGroup.addView(radioButton);
        }
        radioGroup.check(0 + 600);
        frameLayout.addView(radioGroup);
    }

    public String getValue() {
        RadioGroup radioGroup = (RadioGroup) frameLayout.getChildAt(0);
        if (radioGroup == null)
            return "";
        int checkedID = radioGroup.getCheckedRadioButtonId();
        for (int i = 0; i < mSelectItems.size(); i++) {
            if (checkedID == i + 600) {
                return mSelectItems.get(i);
            }
        }
        return "";
    }

    public String getStubValue() {
        RadioGroup radioGroup = (RadioGroup) frameLayout.getChildAt(0);
        if (radioGroup == null)
            return "0";
        int checkedID = radioGroup.getCheckedRadioButtonId();
        return checkedID + "";
    }

    public void recover(String checkID) {
        int id = 0;
        try {
            id = Integer.parseInt(checkID);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        RadioGroup radioGroup = (RadioGroup) frameLayout.getChildAt(0);
        radioGroup.check(id);
    }

    public void disableView() {
        RadioGroup radioGroup = (RadioGroup) frameLayout.getChildAt(0);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(false);
        }
    }

    public void enabledView() {
        RadioGroup radioGroup = (RadioGroup) frameLayout.getChildAt(0);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(true);
        }
    }
}
