package com.zdtco.formui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.CheckBox
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.xh.formlib.R

import java.util.ArrayList

/**
 * Created by G1494458 on 2017/9/4.
 */

class MultiCheckBox : GridLayout {

//    internal var tvTitles: MutableList<TextView> = ArrayList()
    private var checkBoxes: MutableList<CheckBox> = ArrayList()
//    internal var values: MutableList<String> = ArrayList()

    val isEmpty: Boolean
        get() {
            var isSelectNothing = true
            for (checkBox in checkBoxes) {
                if (checkBox.isChecked) {
                    isSelectNothing = false
                    break
                }
            }
            return isSelectNothing
        }

    val checkResult: String
        get() {
//            val results = ArrayList<String>()
//            results.addAll(values)
//            for (checkBox in checkBoxes) {
//                if (!checkBox.isChecked) {
//                    val value = checkBox.getTag(R.string.tag_check_box) as String
//                    results.remove(value)
//                }
//            }
//            val stringBuilder = StringBuilder()
//            for (s in results) {
//                stringBuilder.append(s).append(",")
//            }
//            if (stringBuilder.length > 1) {
//                stringBuilder.deleteCharAt(stringBuilder.length - 1)
//            }

            //获取checkbox结果
            val stringBuilder = StringBuilder()
            for (checkBox in checkBoxes) {
                if (checkBox.isChecked) {
                    stringBuilder.append(checkBox.text).append(",")
                }
            }
            if (stringBuilder.length > 1) {
                stringBuilder.deleteCharAt(stringBuilder.length - 1)
            }
            return stringBuilder.toString()
        }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
//        gravity = Gravity.CENTER_VERTICAL
    }

    fun setCheckTexts(vs: List<String>?) {
        if (vs == null)
            return
        val numCheckBox = vs.size

        columnCount = 3

        for (i in 0 until numCheckBox) {
//            val v = LayoutInflater.from(context)
//                    .inflate(R.layout.widget_maintain_check_box, this, false)
//            val tv = v.findViewById<View>(R.id.value) as TextView
//            val cb = v.findViewById<View>(R.id.check_box) as CheckBox

            val cb = CheckBox(context)
            cb.text = vs[i]
//            cb.setTag(R.string.tag_check_box, vs[i])

//            tvTitles.add(tv)
            checkBoxes.add(cb)
            addView(cb)

        }
//        values.clear()
//        values.addAll(vs)
    }

    fun disableAll() {
        for (checkBox in checkBoxes) {
            checkBox.isEnabled = false
        }
    }

    fun enableAll() {
        for (checkBox in checkBoxes) {
            checkBox.isEnabled = true
        }
    }

    fun setCheckState(state: String) {
        for (checkBox in checkBoxes) {
//            val name = checkBox.getTag(R.string.tag_check_box) as String
            val name = checkBox.text
            checkBox.isChecked = state.contains(name)
        }
    }

}
