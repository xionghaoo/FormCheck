package com.zdtco.formui


import android.content.Context
import android.graphics.Color
import android.support.v4.util.Pair
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.xh.formlib.R
import com.zdtco.FUtil
import com.zdtco.datafetch.data.CJCellValue
import com.zdtco.datafetch.data.GeneralRow
import com.zdtco.datafetch.data.MultiColFormRow
import org.w3c.dom.Text
import java.math.BigDecimal
import java.util.*

/**
 * Created by G1494458 on 2017/11/22.
 * 输入视图
 */
class InputView : FrameLayout {

    private var textView: TextView? = null
    private var editText: EditText? = null
    private var spinner: Spinner? = null
    private var multiCheckBox: MultiCheckBox? = null
    private var multiSelectView: MultiSelectView? = null
    private var cjInputView: CJInputView? = null
    private var spinnerAdapter: ArrayAdapter<String>? = null

    private var type: Int = TYPE_LABEL
    internal var rowName: String? = null
    private var isMustWrite: Boolean = false
    private var hasUndoneItem: Boolean = false

    companion object {
        val ERROR_NONE: String = "无"
        val INPUT_TYPE_CHAR: String = "0123456789/.-"

        val SPINNER_VIEW: Int = R.string.tag_spinner_value
        //行填写值类型
        var TYPE_INVALID: Int = -1         //controlType = 0 无效栏位
        var TYPE_NULL: Int = 0
        var TYPE_EDIT_TEXT: Int = 1         //文本编辑框
        var TYPE_NUMBER_TXT_INPUT: Int = 2   //数值输入框
        var TYPE_PWD_TXT_INPUT: Int = 6     //密码输入框
        var TYPE_MULTI_ROW_TXT_INPUT: Int = 7     //多行输入
        var TYPE_SPINNER: Int = 5          //下拉选框
        var TYPE_CHECK_BOX: Int = 3        //多选框
        var TYPE_LABEL: Int = 8            //普通显示文本
//        var TYPE_RADIO_BUTTON: Int = 4
        var TYPE_CJ_INPUT: Int = 10  //初件集合栏位
        var TYPE_MULTI_SELECT: Int = 4 //单选框

        val TAG_ROW_ID_KEY: Int = R.string.input_view_row_id
        val TAG_ROW_NAME: Int = R.string.input_view_row_name
        val TAG_ROW_EXTRA_TYPE: Int = R.string.input_view_row_extra_type
        val TAG_ROW_ORDER_NO: Int = R.string.input_view_row_order_no
        val TAG_ROW_XJ_ID: Int = R.string.input_view_row_xj_id
        val TAG_ROW_CONTROL_TYPE: Int = R.string.input_view_row_control_type
        val TAG_ROW_INPUT_ERROR: Int = R.string.input_view_row_error
    }

    constructor(context: Context) : super(context) {

    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
    }

    fun initView(row: GeneralRow?) {
        if (row == null) {
            throw Exception("FormRow is null")
        }
        if (row.initValue == null) {
            throw Exception("FormRow initValue is null")
        }
        type = row.inputType
        rowName = row.rowName
        isMustWrite = row.isMustWrite
        setTags(row.rowID, row.indexB, row.indexA, row.rowName, row.rowExtraType)
        when(type) {
            TYPE_LABEL -> {
                textView = LayoutInflater.from(context)
                        .inflate(R.layout.input_view_label, this, false) as TextView
                textView?.text = row.defaultValue
                addView(textView)
            }
            TYPE_EDIT_TEXT -> {
                editText = EditText(context)
                editText?.setText(row.defaultValue)
                addView(editText)
            }
            TYPE_NUMBER_TXT_INPUT -> {
                editText = EditText(context)
                editText?.inputType = InputType.TYPE_CLASS_NUMBER
                editText?.keyListener = DigitsKeyListener.getInstance(INPUT_TYPE_CHAR)
                editText?.setText(row.defaultValue)
                if (row.initValue != "") {
                    val pairMoreValue = resolveMoreValues(row.initValue, false)

                    editText?.hint = "< " + pairMoreValue.first + " , " + pairMoreValue.second + " >"

                    editText?.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {

                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            val result = s.toString()
                            if (!isParamValid(result, pairMoreValue.first.toDouble(), pairMoreValue.second.toDouble())) {
                                editText?.setTextColor(Color.RED)
                                this@InputView.setTag(TAG_ROW_INPUT_ERROR, inputErrorStr(row.rowName, result))
                            } else {
                                editText?.setTextColor(Color.GREEN)
                                setErrorMsg("", false)
                                this@InputView.setTag(TAG_ROW_INPUT_ERROR, "")
                            }

                        }
                    })
                }
                addView(editText)
            }
            TYPE_SPINNER -> {
                spinner = Spinner(context)
                val initValues: List<String>? = row.initValue?.split(",")
                spinner?.adapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, initValues)
                spinner?.setTag(SPINNER_VIEW, 0)
                spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        parent?.setTag(SPINNER_VIEW, position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
                addView(spinner)
            }
            TYPE_CHECK_BOX -> {
                multiCheckBox = MultiCheckBox(context)
                //需要初始化值
                val cbValues = row.initValue?.split(",")
                multiCheckBox?.setCheckTexts(cbValues)
                addView(multiCheckBox)
            }
            TYPE_MULTI_SELECT -> {
                multiSelectView = MultiSelectView(context)
                val sValues = row.initValue?.split(",")
//                val selectItems = "OK,NG".split(",")
                multiSelectView?.setSelectItems(sValues)
                addView(multiSelectView)
            }
            TYPE_CJ_INPUT -> {
                cjInputView = CJInputView(context)
                cjInputView?.initView(row.cjRowDatas)
                addView(cjInputView)
            }
            else -> {
                editText = EditText(context)
                editText?.setText(row.defaultValue)
                addView(editText)
            }
        }

        addErrorTxtView()
    }

    fun initMultiColView(row: MultiColFormRow?) {
        if (row == null) {
            throw Exception("FormRow is null")
        }
        if (row.inputRow.initValue == null) {
            throw Exception("FormRow initValue is null")
        }
        type = row.inputRow.inputType
        rowName = row.title
        isMustWrite = row.inputRow.isMustWrite
        setTags(row.inputRow.rowID,
                "",
                row.inputRow.indexA,
                row.inputRow.rowName,
                -1)
        when(type) {
            TYPE_LABEL -> {
                textView = TextView(context)
                textView?.text = row.inputRow.defaultValue
                addView(textView)
            }
            TYPE_EDIT_TEXT -> {
                editText = EditText(context)
                editText?.setText(row.inputRow.defaultValue)
                addView(editText)
            }
            TYPE_NUMBER_TXT_INPUT -> {
                editText = EditText(context)
                editText?.inputType = InputType.TYPE_CLASS_NUMBER
                editText?.keyListener = DigitsKeyListener.getInstance(INPUT_TYPE_CHAR)
                editText?.setText(row.inputRow.defaultValue)
                if (row.inputRow.initValue != "") {
                    val pairMoreValue = resolveMoreValues(row.inputRow.initValue, false)

                    editText?.hint = "< " + pairMoreValue.first + " , " + pairMoreValue.second + " >"

                    editText?.addTextChangedListener(object : TextWatcher {
                        override fun afterTextChanged(s: Editable?) {

                        }

                        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

                        }

                        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                            val result = s.toString()
                            if (!isParamValid(result, pairMoreValue.first.toDouble(), pairMoreValue.second.toDouble())) {
                                editText?.setTextColor(Color.RED)
                                this@InputView.setTag(TAG_ROW_INPUT_ERROR, inputErrorStr(row.inputRow.rowName, result))
                            } else {
                                editText?.setTextColor(Color.GREEN)
                                setErrorMsg("", false)
                                this@InputView.setTag(TAG_ROW_INPUT_ERROR, "")
                            }

                        }
                    })
                }
                addView(editText)
            }
            TYPE_SPINNER -> {
                spinner = Spinner(context)
                val initValues: List<String>? = row.inputRow.initValue?.split(",")
                spinnerAdapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, initValues)
                spinner?.adapter = spinnerAdapter
                spinner?.setTag(SPINNER_VIEW, 0)
                spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        parent?.setTag(SPINNER_VIEW, position)
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
                addView(spinner)
            }
            TYPE_CHECK_BOX -> {
                multiCheckBox = MultiCheckBox(context)
                //需要初始化值
                val cbValues = row.inputRow.initValue?.split(",")
                multiCheckBox?.setCheckTexts(cbValues)
                addView(multiCheckBox)
            }
            TYPE_MULTI_SELECT -> {
                multiSelectView = MultiSelectView(context)
                val sValues = row.inputRow.initValue?.split(",")
                multiSelectView?.setSelectItems(sValues)
                addView(multiSelectView)
            }
            else -> {
                editText = EditText(context)
                editText?.setText(row.inputRow.defaultValue)
                addView(editText)
            }
        }

        addErrorTxtView()
    }

    public fun addErrorTxtView() {
        val tv = TextView(context)
        tv.setTextColor(Color.RED)
        tv.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.END or Gravity.CENTER_VERTICAL)
        addView(tv)
    }

    private fun setTags(id: String, xjID: String, orderNo: String, name: String, extraType: Int) {
        setTag(TAG_ROW_ID_KEY, id)
        setTag(TAG_ROW_XJ_ID, xjID)
        setTag(TAG_ROW_ORDER_NO, orderNo)
        setTag(TAG_ROW_NAME, name)
        setTag(TAG_ROW_CONTROL_TYPE, type.toString())
        var rowExtraType = ""
        if (extraType != -1) {
            rowExtraType = extraType.toString()
        }
        setTag(TAG_ROW_EXTRA_TYPE, rowExtraType)
    }

    fun getValue() : String? {
        var result: String? = ""
        when(type) {
            TYPE_EDIT_TEXT, TYPE_NUMBER_TXT_INPUT -> {
                result = editText?.text.toString().trim()
            }
            TYPE_SPINNER -> {
                val position: Int = spinner?.getTag(SPINNER_VIEW) as Int
                result = spinnerAdapter?.getItem(position)
            }
            TYPE_CHECK_BOX -> {
                result = multiCheckBox?.checkResult
            }
            TYPE_MULTI_SELECT -> {
                result = multiSelectView?.value
            }
            TYPE_CJ_INPUT -> {
                result = cjInputView?.value
            }
            else -> {
                result = editText?.text.toString().trim()
            }
        }
        return result
    }

    fun getStubValue() : String? {
        var result: String? = ""
        when(type) {
            TYPE_EDIT_TEXT, TYPE_NUMBER_TXT_INPUT -> {
                result = editText?.text.toString().trim()
            }
            TYPE_SPINNER -> {
                val position: Int = spinner?.getTag(SPINNER_VIEW) as Int
                result = position.toString()
            }
            TYPE_CHECK_BOX -> {
                result = multiCheckBox?.checkResult
            }
            TYPE_MULTI_SELECT -> {
                result = multiSelectView?.stubValue
            }
            TYPE_CJ_INPUT -> {
                result = cjInputView?.value
            }
            else -> {
                result = editText?.text.toString().trim()
            }
        }
        return result
    }

    fun recoverValue(value: String) {
        when(type) {
            TYPE_EDIT_TEXT, TYPE_NUMBER_TXT_INPUT -> {
                editText?.setText(value)
            }
            TYPE_SPINNER -> {
                val position: Int = Integer.parseInt(value)
                spinner?.setSelection(position)
            }
            TYPE_CHECK_BOX -> {
                multiCheckBox?.setCheckState(value)
            }
            TYPE_MULTI_SELECT -> {
                multiSelectView?.recover(value)
            }
            TYPE_CJ_INPUT -> {
                cjInputView?.recover(value)
            }
            else -> {
                editText?.setText(value)
            }
        }
    }

    fun setCJCell(values: List<CJCellValue.CjItem>) {
        if (type == TYPE_CJ_INPUT) {
            cjInputView?.setDefineValue(values)
        }
    }

    fun disableView() {
        when(type) {
            TYPE_EDIT_TEXT, TYPE_NUMBER_TXT_INPUT -> {
                editText?.isEnabled = false
            }
            TYPE_SPINNER -> {
                spinner?.isEnabled = false
            }
            TYPE_CHECK_BOX -> {
                multiCheckBox?.disableAll()
            }
            TYPE_MULTI_SELECT -> {
                multiSelectView?.disableView()
            }
            TYPE_CJ_INPUT -> {
                cjInputView?.disableView()
            }
            else -> {
                editText?.isEnabled = false
            }
        }
    }

    fun enabledView() {
        when(type) {
            TYPE_EDIT_TEXT, TYPE_NUMBER_TXT_INPUT -> {
                editText?.isEnabled = true
            }
            TYPE_SPINNER -> {
                spinner?.isEnabled = true
            }
            TYPE_CHECK_BOX -> {
                multiCheckBox?.enableAll()
            }
            TYPE_MULTI_SELECT -> {
                multiSelectView?.enabledView()
            }
            TYPE_CJ_INPUT -> {
                cjInputView?.enabledView()
            }
            else -> {
                editText?.isEnabled = true
            }
        }
    }

    public fun checkInputValidity() : String {
        if (isMustWrite && getValue() == "") {
            return rowName + " 项目为空"
        }

        when(type) {
            TYPE_NUMBER_TXT_INPUT -> {
                val error = getTag(TAG_ROW_INPUT_ERROR)
                if (error == null) {
                    return ""
                } else {
                    return error as String
                }
            }
            TYPE_SPINNER -> {
                return ""
            }
            TYPE_CHECK_BOX -> {
                val selectItems = getValue()
                if (selectItems!!.contains("NG") || selectItems.contains("N")) {
                    return rowName + " 选择项包含NG或N"
                } else {
                    return ""
                }
            }
            TYPE_MULTI_SELECT -> {
                val selectItem = getValue()
                if (selectItem == "NG" || selectItem == "N") {
                    return rowName + " 选择项为NG或N"
                } else {
                    return ""
                }
            }
            TYPE_CJ_INPUT -> {
                return cjInputView?.checkInputValidity()!!
            }
            else -> {
                return ""
            }
        }
    }

    private fun isParamValid(param: String?, lowerLimit: Double, upperLimit: Double): Boolean {
        if (param == "") {
            setErrorMsg("填写项为空", true)
            return false
        }

        var inputParam = 0.0
        //檢查輸入的是否是數字
        try {
            inputParam = java.lang.Double.parseDouble(param)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            setErrorMsg("格式錯誤", true)
            return false
        }

//        val lowerLimit = pairMoreValue.first.toDouble()
//        val upperLimit = pairMoreValue.second.toDouble()

        if (lowerLimit != -100.0 && inputParam < lowerLimit) {
//            FUtil.showToast(context, "$name: $param 超出下限$lowerLimit，請輸入正確的值")
            setErrorMsg("超出下限$lowerLimit", true)
            return false
        }

        if (upperLimit != -100.0 && inputParam > upperLimit) {
//            FUtil.showToast(context, "$name: $param 超出上限$upperLimit，請輸入正確的值")
            setErrorMsg("超出上限$upperLimit", true)
            return false
        }
        return true
    }

    private fun inputErrorStr(name: String, param: String) : String {
        return "<" + name + ": " + param + ">" + " 超出指定范围"
    }

    protected fun resolveMoreValues(values: String?, display: Boolean): Pair<String, String> {
        val limits: Array<String>
        var strLower: String? = null
        var strUpper: String? = null

        if (values != null && values != "") {
            if (values.contains("|")) {
                limits = values.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val lower = limits[1]
                val upper = limits[0]
                val lowLimit = java.lang.Double.parseDouble(upper) - java.lang.Double.parseDouble(lower)
                val upLimit = java.lang.Double.parseDouble(upper) + java.lang.Double.parseDouble(lower)
                val lowbd = BigDecimal(lowLimit)
                val d1 = lowbd.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                val upperbd = BigDecimal(upLimit)
                val d2 = upperbd.setScale(2, BigDecimal.ROUND_HALF_UP).toDouble()
                strLower = d1.toString()
                strUpper = d2.toString()
            } else {
                limits = values.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                if (limits.size > 1) {
                    strLower = limits[0].substring(2)
                    strUpper = limits[1].substring(2)
                } else if (limits.size == 1) {
                    if (limits[0].contains("<=")) {
                        strUpper = limits[0].substring(2)
                    } else if (limits[0].contains(">=")) {
                        strLower = limits[0].substring(2)
                    } else if (limits[0].contains("<")) {
                        strUpper = limits[0].substring(1)
                    } else if (limits[0].contains(">")) {
                        strLower = limits[0].substring(1)
                    }
                }
            }
        }
        if (display) {
            if (strLower == null || strLower == "") {
                strLower = "∞"
            }
            if (strUpper == null || strUpper == "") {
                strUpper = "∞"
            }
        } else {
            if (strLower == null || strLower == "") {
                strLower = "-100"
            }
            if (strUpper == null || strUpper == "") {
                strUpper = "-100"
            }
        }
        return Pair.create(strLower, strUpper)
    }

    private fun setErrorMsg(error: String, show: Boolean) {
        val tv = getChildAt(1) as TextView
        if (show) {
            tv.visibility = View.VISIBLE
            tv.text = error
        } else {
            tv.visibility = View.GONE
        }

    }

}