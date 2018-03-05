package com.zdtco.formui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout

import com.xh.formlib.R
import com.zdtco.datafetch.data.CJCellValue
import kotlinx.android.synthetic.main.widget_maintain_check_box.view.*

/**
 * Created by G1494458 on 2018/1/5.
 */

class FormRowView : FrameLayout {

    private var mType = ROW_TYPE_SIMPLE

    val inputResult: Result?
        get() {
            var postRow: Result? = null
            when (mType) {
                ROW_TYPE_SIMPLE -> {
                    val iv: InputView = findViewById<InputView>(R.id.input_view)
                    val rowName: String = iv.getTag(InputView.TAG_ROW_NAME) as String
                    val rowID: String = iv.getTag(InputView.TAG_ROW_ID_KEY) as String
                    val rowExtraType: String = iv.getTag(InputView.TAG_ROW_EXTRA_TYPE) as String
                    val orderNo: String = iv.getTag(InputView.TAG_ROW_ORDER_NO) as String
                    val controlType: String = iv.getTag(InputView.TAG_ROW_CONTROL_TYPE) as String

                    val value = iv.getValue()
                    postRow = Result(rowName, value, rowID, rowExtraType, controlType, orderNo)
                }
                ROW_TYPE_MULTI_COLUMN -> {
                    val iv: InputView = findViewById<InputView>(R.id.input_view)
                    val rowName: String = iv.getTag(InputView.TAG_ROW_NAME) as String
                    val rowID: String = iv.getTag(InputView.TAG_ROW_ID_KEY) as String
                    val rowExtraType: String = iv.getTag(InputView.TAG_ROW_EXTRA_TYPE) as String
                    val orderNo: String = iv.getTag(InputView.TAG_ROW_ORDER_NO) as String

                    val controlType: String = iv.getTag(InputView.TAG_ROW_CONTROL_TYPE) as String

                    val value = iv.getValue()
                    postRow = Result(rowName, value, rowID, rowExtraType, controlType, orderNo)
                }
                ROW_TYPE_CJ -> {
                    val iv: InputView = findViewById<InputView>(R.id.cj_input_view)
                    val rowName: String = iv.getTag(InputView.TAG_ROW_NAME) as String
                    val rowID: String = iv.getTag(InputView.TAG_ROW_ID_KEY) as String
                    val rowExtraType: String = iv.getTag(InputView.TAG_ROW_EXTRA_TYPE) as String
                    val orderNo: String = iv.getTag(InputView.TAG_ROW_ORDER_NO) as String
                    val controlType: String = iv.getTag(InputView.TAG_ROW_CONTROL_TYPE) as String

                    val value = iv.getValue()

                    val iv2: InputView = findViewById<InputView>(R.id.inputView)
                    val value2 = iv2.getValue()
                    postRow = Result(rowName,
                            value2 + ";" + value, rowID, rowExtraType, controlType, orderNo)
                }
                ROW_TYPE_COMMENT -> {
                    val commentView = findViewById<EditText>(R.id.comment)
                    val comment = commentView.text.toString().trim()
                    postRow = Result("comment",
                            comment, "", "", "", "")
                }
                ROW_TYPE_XJ -> {
                    val iv: InputView = findViewById<InputView>(R.id.input_view)
                    val rowName: String = iv.getTag(InputView.TAG_ROW_NAME) as String
                    val rowID: String = iv.getTag(InputView.TAG_ROW_XJ_ID) as String
                    val rowExtraType: String = iv.getTag(InputView.TAG_ROW_EXTRA_TYPE) as String
                    val orderNo: String = iv.getTag(InputView.TAG_ROW_ORDER_NO) as String
                    val controlType: String = iv.getTag(InputView.TAG_ROW_CONTROL_TYPE) as String

                    val value = iv.getValue()
                    postRow = Result(rowName, value, rowID, rowExtraType, controlType, orderNo)
                }
                else -> {
                    postRow = null
                }
            }
            return postRow
        }

    val recoverResult: Result?
        get() {
            var postRow: Result? = null
            when (mType) {
                ROW_TYPE_SIMPLE -> {
                    val iv: InputView = findViewById<InputView>(R.id.input_view)
                    val rowName: String = iv.getTag(InputView.TAG_ROW_NAME) as String
                    val rowID: String = iv.getTag(InputView.TAG_ROW_ID_KEY) as String
                    val rowExtraType: String = iv.getTag(InputView.TAG_ROW_EXTRA_TYPE) as String
                    val orderNo: String = iv.getTag(InputView.TAG_ROW_ORDER_NO) as String
                    val controlType: String = iv.getTag(InputView.TAG_ROW_CONTROL_TYPE) as String

                    val value = iv.getStubValue()
                    postRow = Result(rowName, value, rowID, rowExtraType, controlType, orderNo)
                }
                ROW_TYPE_MULTI_COLUMN -> {
                    val iv: InputView = findViewById<InputView>(R.id.input_view)
                    val rowName: String = iv.getTag(InputView.TAG_ROW_NAME) as String
                    val rowID: String = iv.getTag(InputView.TAG_ROW_ID_KEY) as String
                    val rowExtraType: String = iv.getTag(InputView.TAG_ROW_EXTRA_TYPE) as String
                    val orderNo: String = iv.getTag(InputView.TAG_ROW_ORDER_NO) as String
                    val controlType: String = iv.getTag(InputView.TAG_ROW_CONTROL_TYPE) as String

                    val value = iv.getStubValue()
                    postRow = Result(rowName, value, rowID, rowExtraType, controlType, orderNo)
                }
                ROW_TYPE_CJ -> {
                    val iv: InputView = findViewById<InputView>(R.id.cj_input_view)
                    val rowName: String = iv.getTag(InputView.TAG_ROW_NAME) as String
                    val rowID: String = iv.getTag(InputView.TAG_ROW_ID_KEY) as String
                    val rowExtraType: String = iv.getTag(InputView.TAG_ROW_EXTRA_TYPE) as String
                    val orderNo: String = iv.getTag(InputView.TAG_ROW_ORDER_NO) as String
                    val controlType: String = iv.getTag(InputView.TAG_ROW_CONTROL_TYPE) as String

                    val value = iv.getStubValue()

                    val iv2: InputView = findViewById<InputView>(R.id.inputView)
                    val value2 = iv2.getStubValue()

                    postRow = Result(rowName,
                            value2 + ";" + value, rowID, rowExtraType, controlType, orderNo)
                }
                ROW_TYPE_COMMENT -> {
                    val commentView = findViewById<EditText>(R.id.comment)
                    val comment = commentView.text.toString().trim()
                    postRow = Result("comment",
                            comment, "", "", "", "")
                }
                ROW_TYPE_XJ -> {
                    val iv: InputView = findViewById<InputView>(R.id.input_view)
                    val rowName: String = iv.getTag(InputView.TAG_ROW_NAME) as String
                    val rowID: String = iv.getTag(InputView.TAG_ROW_XJ_ID) as String
                    val rowExtraType: String = iv.getTag(InputView.TAG_ROW_EXTRA_TYPE) as String
                    val orderNo: String = iv.getTag(InputView.TAG_ROW_ORDER_NO) as String
                    val controlType: String = iv.getTag(InputView.TAG_ROW_CONTROL_TYPE) as String

                    val value = iv.getValue()
                    postRow = Result(rowName, value, rowID, rowExtraType, controlType, orderNo)
                }
                else -> {
                    postRow = null
                }
            }
            return postRow
        }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}

    fun create(type: Int, hasFormula: Boolean) : FormRowView {
        val inflater = LayoutInflater.from(context)
        val v: View
        mType = type
        when (type) {
            ROW_TYPE_SIMPLE -> v = inflater.inflate(R.layout.form_row_single, this, false)
            ROW_TYPE_MULTI_COLUMN -> v = inflater.inflate(R.layout.form_row_multi_single, this, false)
            ROW_TYPE_CJ -> v = inflater.inflate(R.layout.form_row_cj, this, false)
            ROW_TYPE_COMMENT -> v = inflater.inflate(R.layout.form_view_comment, this, false)
            ROW_TYPE_XJ -> v = inflater.inflate(R.layout.form_row_xj, this, false)
            else -> v = inflater.inflate(R.layout.form_row_single, this, false)
        }
        addView(v)
        if (type != ROW_TYPE_COMMENT && hasFormula) {
            val btn = Button(context)
            btn.text = "计算"
            btn.layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL or Gravity.END)
            addView(btn)
        }
        return this
    }

    public fun setOnFormulaCalculateListener(listener: OnFormulaCalculateListener) {
        val v = getChildAt(1)
        v?.setOnClickListener {
            listener.onCalculate(this@FormRowView)
        }
    }

    public fun recover(value: String) {
        when (mType) {
            ROW_TYPE_SIMPLE, ROW_TYPE_XJ -> {
                val iv: InputView = findViewById<InputView>(R.id.input_view)
                iv.recoverValue(value)
            }
            ROW_TYPE_MULTI_COLUMN -> {
                val iv: InputView = findViewById<InputView>(R.id.input_view)
                iv.recoverValue(value)
            }
            ROW_TYPE_CJ -> {
                val recValue = value.split(";")
                if (recValue.size > 1) {
                    val iv2: InputView = findViewById<InputView>(R.id.inputView)
                    iv2.recoverValue(recValue[0])

                    val iv: InputView = findViewById<InputView>(R.id.cj_input_view)
                    iv.recoverValue(recValue[1])
                }
            }
            ROW_TYPE_COMMENT -> {
                val commentView = findViewById<EditText>(R.id.comment)
                commentView.setText(value)
            }
            else -> {

            }
        }
    }

    public fun setCJCell(values: List<CJCellValue.CjItem>) {
        if (mType == ROW_TYPE_CJ) {
            val iv: InputView = findViewById<InputView>(R.id.cj_input_view)
            iv.setCJCell(values)
        }
    }

    public fun enabledRowView() {
        when (mType) {
            ROW_TYPE_SIMPLE, ROW_TYPE_XJ -> {
                val iv: InputView = findViewById<InputView>(R.id.input_view)
                iv.enabledView()
            }
            ROW_TYPE_MULTI_COLUMN -> {
                val iv: InputView = findViewById<InputView>(R.id.input_view)
                iv.enabledView()
            }
            ROW_TYPE_CJ -> {
                val iv: InputView = findViewById<InputView>(R.id.cj_input_view)
                iv.enabledView()

                val iv2: InputView = findViewById<InputView>(R.id.inputView)
                iv2.enabledView()
            }
            ROW_TYPE_COMMENT -> {
                val commentView = findViewById<EditText>(R.id.comment)
                commentView.isEnabled = true
            }
            else -> {

            }
        }
    }

    public fun disableRowView() {
        when (mType) {
            ROW_TYPE_SIMPLE, ROW_TYPE_XJ -> {
                val iv: InputView = findViewById<InputView>(R.id.input_view)
                iv.disableView()
            }
            ROW_TYPE_MULTI_COLUMN -> {
                val iv: InputView = findViewById<InputView>(R.id.input_view)
                iv.disableView()
            }
            ROW_TYPE_CJ -> {
                val iv: InputView = findViewById<InputView>(R.id.cj_input_view)
                iv.disableView()

                val iv2: InputView = findViewById<InputView>(R.id.inputView)
                iv2.disableView()
            }
            ROW_TYPE_COMMENT -> {
                val commentView = findViewById<EditText>(R.id.comment)
                commentView.isEnabled = false
            }
            else -> {

            }
        }
    }

    public fun showCalculateButton() {
        val v = getChildAt(1)
        v?.visibility = View.VISIBLE
    }

    public fun setRowID(id: String) {
        setTag(ROW_ID, id)
    }

    public fun getRowID() : String {
//        val v = getChildAt(1)
        return getTag(ROW_ID) as String
    }

    public fun checkInputValidity() : String {
        when (mType) {
            ROW_TYPE_SIMPLE, ROW_TYPE_XJ, ROW_TYPE_MULTI_COLUMN -> {
                val iv: InputView = findViewById<InputView>(R.id.input_view)
                return iv.checkInputValidity()
            }
            ROW_TYPE_CJ -> {
                val iv: InputView = findViewById<InputView>(R.id.cj_input_view)
                return iv.checkInputValidity()
            }
//            ROW_TYPE_COMMENT -> {
//                val commentView = findViewById<EditText>(R.id.comment)
//                commentView.isEnabled = false
//            }
            else -> {
                return ""
            }
        }
    }

    public fun getOrder() : String {
        var orderNo = ""
        when (mType) {
            ROW_TYPE_SIMPLE, ROW_TYPE_XJ -> {
                val iv: InputView = findViewById<InputView>(R.id.input_view)
                orderNo = iv.getTag(InputView.TAG_ROW_ORDER_NO) as String
            }
            ROW_TYPE_MULTI_COLUMN -> {
                val iv: InputView = findViewById<InputView>(R.id.input_view)
                orderNo = iv.getTag(InputView.TAG_ROW_ORDER_NO) as String

            }
            ROW_TYPE_CJ -> {
                val iv: InputView = findViewById<InputView>(R.id.cj_input_view)
                orderNo = iv.getTag(InputView.TAG_ROW_ORDER_NO) as String

            }
        }
        return orderNo
    }

    companion object {

        val ROW_TYPE_SIMPLE = 0
        val ROW_TYPE_MULTI_COLUMN = 1
        val ROW_TYPE_CJ = 2
        val ROW_TYPE_COMMENT = 3
        val ROW_TYPE_XJ = 4

        val ROW_ID = R.string.form_row_view_id
    }

    interface OnFormulaCalculateListener {
        fun onCalculate(row: FormRowView)
    }

    inner class Result(val rowName: String,
                       var value: String?,
                       val rowID: String,
                       val extraID: String,
                       val controlType: String,
                       val orderNo: String)
}
