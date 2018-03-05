package com.zdtco.formui

import android.content.Context
import android.support.annotation.WorkerThread
import android.support.v7.app.AlertDialog
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.gson.Gson
import com.xh.formlib.R
import com.zdtco.FUtil
import com.zdtco.datafetch.data.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import kotlin.concurrent.timerTask

/**
 * Created by G1494458 on 2017/11/22.
 * 测试表单
 */
class FormView : LinearLayout {

    private val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINA)

    private var formID: String? = null

    private var formName: String? = null
    public var workNo: String? = null
    private var machineID: String? = null
    private var machineName: String? = null
    private var formType: String? = null
    private var startTime: String? = null

    //    private var clsr: String? = null
    private var comment: String? = null

    //只针对初件表单
    public var partNo: String = ""
    public var lineNo: String = ""

    private var mHasComment: Boolean = true

    private var adapter: RowAdapter? = null
    private var callback: Callback? = null

    private var mType: Int? = null

    private var timer: Timer? = null
    private var timerSeconds: Long? = null

    public var checkStartTime: String? = null   //生产日报表点检项开始时间

    private val formulaMap = HashMap<String, FormRowFormula>()

    companion object {
        val TYPE_GENERAL: Int = 1      //Maintenance/MachineNoScan.aspx,ReportMode1.aspx 模板1通用表单
        val TYPE_CJ: Int = 2 //Maintenance/MachineNoScan.aspx,ReportModeMain.aspx  初件表单
        val TYPE_FOUR: Int = 4 //Maintenance/MachineNoScan.aspx,ReportMode4.aspx 模板四
        val TYPE_MAINTENANCE: Int = 6  //Maintenance/MachineNoScan.aspx,ReportMode6.aspx 保养表单
        val TYPE_FIVE: Int = 5  //Maintenance/MachineNoScan.aspx,ReportMode5.aspx  模板5
        val TYPE_XJ: Int = 7  //Maintenance/MachineNoScan.aspx,ReportMode7.aspx  巡检表
        val TYPE_HC: Int = 11
//        var TYPE_MULTI_COLUMN_MERGE: Int = 3

        fun getPostDataForMergeString(f1: FormView, f2: FormView) : String {
            return Gson().toJson(getPostDataForMerge(f1, f2))
        }

        private fun getPostDataForMerge(f1: FormView, f2: FormView) : FormPostData {
            val pd1 = f1.getPostDataResult()
            val pd2 = f2.getPostDataResult()
            pd1.postRows.addAll(pd2.postRows)
            pd1.comment = pd2.comment
            return pd1
        }

        private fun getPrintDataForMerge(f1: FormView, f2: FormView) : FormPrintData {
            val pd1 = f1.getFormPrintData()
            val pd2 = f2.getFormPrintData()
            pd1.printRows.addAll(pd2.printRows)
            pd1.comment = pd2.comment
            return pd1
        }

        fun getTemplateFivePostData(f1: FormView, f2: FormView, postDataList: MutableList<FormPostData>) : String {
            val pd1 = f1.getPostDataResult()
            val pd2 = f2.getPostDataResult()

            val tmp = FormPostData()
            for (p in postDataList) {
                tmp.postRows.addAll(p.postRows)
            }
            tmp.postRows.addAll(pd2.postRows)

            val result = TemplateFiveFormPostData(pd1.workNo,
                    pd1.machineID, pd1.formID, pd1.formType,
                    pd1.clsr, pd1.comment, pd1.startTime, pd1.endTime, (postDataList.size + 1).toString())

            for (row in pd1.postRows) {
                result.header.add(TemplateFiveFormPostData.FormHeader(row.rowExtraType,
                        row.rowID, row.rowName, row.rowValue, row.orderNo))
            }

            for (i in tmp.postRows.indices) {
                val row = tmp.postRows[i]
                result.content.add(TemplateFiveFormPostData.FormContent(
                        row.rowExtraType, row.rowID, row.rowName, row.rowValue, i, row.orderNo))
            }

            return Gson().toJson(result)
        }
    }

    constructor(context: Context) : super(context) {
    }

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
        init(context, attributeSet)
    }

    private fun init(context: Context, attributeSet: AttributeSet) {
        orientation = VERTICAL

    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun setAdapter(rowAdapter: RowAdapter?, hasComment: Boolean) {
        if (rowAdapter == null)
            return
        checkCallbackNull()

        formID = rowAdapter.form?.formID
        adapter = rowAdapter
        mType = rowAdapter.form?.columnNum
        when(mType) {
            TYPE_GENERAL -> {
                createGeneralForm(rowAdapter.form?.columnNum, rowAdapter.form?.generalForm?.generalRows)}
            TYPE_MAINTENANCE -> createMultiColumnForm(rowAdapter.form?.columnNum, rowAdapter.form?.multiColumnForm?.multiColFormRows)
            TYPE_CJ -> createCJForm(rowAdapter.form?.columnNum, rowAdapter.form?.generalForm?.generalRows)
            TYPE_XJ -> createXJForm(rowAdapter.form?.columnNum, rowAdapter.form?.generalForm?.generalRows)
//            TYPE_MULTI_COLUMN_MERGE -> createMergeMultiColumnForm(rowAdapter.colNum, rowAdapter.mergeMultiRowItems)
            else -> return
        }

        if (hasComment) {
            addView(FormRowView(context).create(FormRowView.ROW_TYPE_COMMENT, false))
        }

        mHasComment = hasComment

        timer = Timer()
        timerSeconds = rowAdapter.form?.timeLimit?.times(60L)
        if (timerSeconds == null) {
            timerSeconds = 0L
        }

        if (timerSeconds!! > 0) {
            timer?.scheduleAtFixedRate(timerTask {
                checkCallbackNull()
                if (timerSeconds!! <= 0) {
                    callback?.timerEnd()
                    timer?.cancel()
                }
                callback?.timeDec(timerSeconds)
                Log.d("FormViewTask", "time " + timerSeconds)
                timerSeconds = timerSeconds!! - 1
            }, 0, 1000)
        }
    }

    fun cancelTimeTask() {
        timer?.cancel()
    }

    private fun createGeneralForm(formColNum: Int?, rowItem: MutableList<GeneralRow>?) {
        if (rowItem == null)
            return

        val tmpMap = analyseGeneralFormFormula(rowItem)

        for (i: Int in rowItem.indices) {
            if (rowItem[i].inputType != InputView.TYPE_INVALID) {
                val v: View? = adapter?.getRowView(i, formColNum, this, tmpMap.containsKey(rowItem[i].rowID))
                adapter?.bindView(rowItem[i], v)
                addView(v)

                val rowView = v as FormRowView
                configRowFormulaCalculator(rowItem[i].rowID, rowView)
            }
        }
    }

    private fun createMultiColumnForm(formColNum: Int?, colRowItem: MutableList<MultiColFormRow>?) {
        if (colRowItem == null)
            return

        val tmpMap = analyseMultiColFormFormula(colRowItem)

        for (i: Int in colRowItem.indices) {
            if (colRowItem[i].inputRow.inputType != InputView.TYPE_INVALID) {
                val v: View? = adapter?.getRowView(i, formColNum, this, tmpMap.containsKey(colRowItem[i].inputRow.rowID))
                adapter?.bindView(colRowItem[i], v)
                addView(v)
            }
        }
    }

    private fun createCJForm(formColNum: Int?, colRowItem: MutableList<GeneralRow>?) {
        if (colRowItem == null)
            return

        val tmpMap = analyseGeneralFormFormula(colRowItem)

        for (i: Int in colRowItem.indices) {
            if (colRowItem[i].inputType != InputView.TYPE_INVALID) {
                val v: View? = adapter?.getRowView(i, formColNum, this, tmpMap.containsKey(colRowItem[i].rowID))
                adapter?.bindCJView(colRowItem[i], v)
                addView(v)

                val rowView = v as FormRowView
                configRowFormulaCalculator(colRowItem[i].rowID, rowView)
            }
        }
    }

    private fun createXJForm(formColNum: Int?, rowItem: MutableList<GeneralRow>?) {
        if (rowItem == null)
            return

        val tmpMap = analyseGeneralFormFormula(rowItem)

        for (i: Int in rowItem.indices) {
            if (rowItem[i].inputType != InputView.TYPE_INVALID) {
                val v: View? = adapter?.getRowView(i, formColNum, this, tmpMap.containsKey(rowItem[i].rowID))
                adapter?.bindXJView(rowItem[i], v)
                addView(v)

                val rowView = v as FormRowView
                configRowFormulaCalculator(rowItem[i].rowID, rowView)
            }
        }
    }

    private fun analyseGeneralFormFormula(rowItem: MutableList<GeneralRow>) : HashMap<String, Boolean> {
        val fMap = HashMap<String, Boolean>()

        for (item1 in rowItem) {
            if (item1.formulaExecuteRow != "") {
                for (item2 in rowItem) {
                    if (item2.rowID == item1.formulaExecuteRow) {
                        fMap.put(item2.rowID, true)
                    }

                    if (item2.rowID == item1.formulaExecuteRow && item1.initValue != "") {
                        //找到公式设定行
                        val formulaID = item1.initValue.substring(item1.initValue.indexOf("]") + 1)
                        callback?.loadFormula(formulaID, object : FormulaCallback {
                            override fun formulaQueryResult(formula: Formula) {
                                formulaMap.put(item2.rowID, FormRowFormula(item2.rowID, formula.key, item1.formulaParameterRows))
                            }

                            override fun formulaCalculateResult(result: String) {

                            }

                            override fun error(error: Throwable?) {

                            }
                        })
                        break
                    }
                }

            }
        }
        return fMap
    }

    private fun analyseMultiColFormFormula(rowItem: MutableList<MultiColFormRow>) : HashMap<String, Boolean> {
        val fMap = HashMap<String, Boolean>()
        for (item1 in rowItem) {
            if (item1.inputRow.formulaExecuteRow != "") {
                for (item2 in rowItem) {
                    if (item2.inputRow.rowID == item1.inputRow.formulaExecuteRow) {
                        fMap.put(item2.inputRow.rowID, true)
                    }

                    if (item2.inputRow.rowID == item1.inputRow.formulaExecuteRow && item1.inputRow.initValue != "") {
                        //找到公式设定行
                        val formulaID = item1.inputRow.initValue.substring(item1.inputRow.initValue.indexOf("]") + 1)
                        callback?.loadFormula(formulaID, object : FormulaCallback {
                            override fun formulaQueryResult(formula: Formula) {
                                formulaMap.put(item2.inputRow.rowID, FormRowFormula(item2.inputRow.rowID, formula.key, item1.inputRow.formulaParameterRows))
                            }

                            override fun formulaCalculateResult(result: String) {

                            }

                            override fun error(error: Throwable?) {

                            }
                        })
                        break
                    }
                }

            }
        }
        return fMap
    }

    private fun configRowFormulaCalculator(rowID: String, rowView: FormRowView) {
        rowView.setRowID(rowID)
        rowView.setOnFormulaCalculateListener(object : FormRowView.OnFormulaCalculateListener {
            override fun onCalculate(row: FormRowView) {
                val formula = formulaMap.get(row.getRowID())
                if (formula != null) {
                    val params = formula.params.split(",")
                    val sb = StringBuilder()
                    for (pRowID in params) {
                        sb.append(getParameterFormRow(pRowID)).append(",")
                    }
                    callback?.calculateFormula(formula.formula, sb.toString(), object : FormulaCallback {
                        override fun formulaQueryResult(formula: Formula) {

                        }

                        override fun formulaCalculateResult(result: String) {
                            rowView.recover(result)
                        }

                        override fun error(error: Throwable?) {

                        }
                    })
                } else {
                    Toast.makeText(context, "无计算公式", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun getParameterFormRow(rowId: String) : String {
        for (i in 0..(childCount - 1)) {
            val row = getChildAt(i) as FormRowView
            if (row.getRowID() == rowId) {
                val result = row.inputResult?.value
                if (result == null) {
                    return ""
                } else {
                    return result
                }
            }
        }
        return ""
    }

    fun initialForm(workNo: String?, machineID: String?, formType: String?) {
        this.workNo = workNo
        this.machineID = machineID
        this.formType = formType
        this.startTime = dateFormat.format(System.currentTimeMillis())
    }

    fun getPostStringResults() : String {
        return Gson().toJson(getPostDataResult())
    }

    fun getErrorMsg(isShow: Boolean) : String {
        val viewCount: Int
        if (mHasComment) {
            viewCount = childCount - 2
        } else {
            viewCount = childCount - 1
        }

        val errorList = StringBuilder()
        for (i in 0..viewCount) {
            val rowView: FormRowView = getChildAt(i) as FormRowView
            val error = rowView.checkInputValidity()
            if (error != "") {
                if (isShow) {
                    errorList./*append((i + 1).toString()).append("、").*/append(error).append("\n")
                } else {
                    errorList.append(error).append(",")
                }
            }
        }

        val result = errorList.toString()
        if (result != "") {
            return errorList.toString()
        } else {
            return InputView.ERROR_NONE
        }
    }

    public fun getPostDataResult() : FormPostData {
        val endTime = dateFormat.format(System.currentTimeMillis())

        val comment: String?
        val viewCount: Int
        if (mHasComment) {
            viewCount = childCount - 2
            val rowView: FormRowView = getChildAt(childCount - 1) as FormRowView
            comment = rowView.inputResult?.value          //填写值
        } else {
            viewCount = childCount - 1
            comment = ""
        }

        val errorList = StringBuilder()

        var error: String
        if (adapter?.form?.isTestForm!!) {
            error = getErrorMsg(false)
        } else {
            error = ""
        }
        val postData: FormPostData = FormPostData(workNo, formID, machineID,
                formType, startTime, endTime, getClsr(), error, comment)
        for (i in 0..viewCount) {
            val rowView: FormRowView = getChildAt(i) as FormRowView
            val result: FormRowView.Result? = rowView.inputResult

            //只针对生产日报表
            if (result?.rowName == "开始时间") {
                result.value = checkStartTime
            }
            if (result?.rowName == "结束时间") {
                result.value = endTime
            }

            //只针对初件表单
            if (result?.rowName == "料号") {
                partNo = result.value!!
            }
            if (result?.rowName == "线体") {
                lineNo = result.value!!
            }

            val postRow: FormPostData.FormPostRow =
                    FormPostData.FormPostRow(result?.rowName, result?.value, result?.rowID, result?.extraID, result?.controlType)
            postRow.orderNo = result?.orderNo
            postData.postRows.add(postRow)

//            val error = rowView.checkInputValidity()
//            if (error != "") {
//                errorList.append(error).append("\n")
//            }
        }

        return postData
    }

    public fun getFormPrintData() : FormPrintData {
        val endTime = dateFormat.format(System.currentTimeMillis())

        val comment: String?
        val viewCount: Int
        if (mHasComment) {
            viewCount = childCount - 2
            val rowView: FormRowView = getChildAt(childCount - 1) as FormRowView
            comment = rowView.recoverResult?.value
        } else {
            viewCount = childCount - 1
            comment = ""
        }

        val printData: FormPrintData = FormPrintData(formID!!, machineID!!, workNo,
                formType, startTime, endTime, getClsr(), comment)
        for (i in 0..viewCount) {
            val rowView: FormRowView = getChildAt(i) as FormRowView
            val result: FormRowView.Result? = rowView.recoverResult      //暂存值或显示值
            val postRow: FormPrintRow =
                    FormPrintRow(result?.rowName, result?.value, result?.rowID, result?.extraID)
            printData.printRows.add(postRow)
        }

        return printData
    }

    private fun getClsr() : String {
        val calendarStart: Calendar = Calendar.getInstance()
        calendarStart.set(Calendar.HOUR_OF_DAY, 8)
        calendarStart.set(Calendar.MINUTE, 0)
        val calendarEnd: Calendar = Calendar.getInstance()
        calendarEnd.set(Calendar.HOUR_OF_DAY, 20)
        calendarEnd.set(Calendar.MINUTE, 30)
        val calendarNow: Calendar = Calendar.getInstance()
        var clsr: String = "白班"
        if (calendarNow.after(calendarStart) && calendarNow.before(calendarEnd)) {
            clsr = "白班"
        } else {
            clsr = "夜班"
        }
        return clsr
    }

    fun saveForm(success: Boolean) {
        if (callback == null) {
            throw Exception("saveForm form must implement Callback interface")
        }
        val data = getFormPrintData()
        data.hasPost = success
        data.postData = getPostStringResults()
        callback?.saveFormPrintData(data)
    }

    /**
     * success 提交成功的标志
     */
    fun saveFormWith(formView: FormView, success: Boolean) {
        if (callback == null) {
            throw Exception("saveForm form must implement Callback interface")
        }
        val data = FormView.getPrintDataForMerge(this, formView)
        data.hasPost = success
        data.postData = FormView.getPostDataForMergeString(this, formView)
        callback?.saveFormPrintData(data)
    }

    fun temporarySaveMerge(mergeIndex: Int) {
        checkCallbackNull()
        val viewCount: Int
        val commentTxt: String?

        if (mHasComment) {
            viewCount = childCount - 2
            val rowView: FormRowView = getChildAt(childCount - 1) as FormRowView
            commentTxt = rowView.recoverResult?.value
        } else {
            viewCount = childCount - 1
            commentTxt = ""
        }

        var formStub: FormStub = FormStub(machineID!!, formID!!, commentTxt)
        formStub.mergeIndex = mergeIndex

        for (i in 0..viewCount) {
            val rowView: FormRowView = getChildAt(i) as FormRowView
            val result: FormRowView.Result? = rowView.recoverResult      //暂存值或显示值
            val rowStub: FormRowStub = FormRowStub(result?.rowID, result?.value)
            formStub.rowStubs.add(rowStub)
        }

        callback?.temporarySaveForm(formStub)
    }

    fun temporarySave() {
        if (callback == null) {
            throw Exception("temporarySave form must implement Callback interface")
        }
        val viewCount: Int
        val commentTxt: String?

        if (mHasComment) {
            viewCount = childCount - 2
            val rowView: FormRowView = getChildAt(childCount - 1) as FormRowView
            commentTxt = rowView.recoverResult?.value
        } else {
            viewCount = childCount - 1
            commentTxt = ""
        }

        var formStub: FormStub = FormStub(machineID!!, formID!!, commentTxt)

        for (i in 0..viewCount) {
            val rowView: FormRowView = getChildAt(i) as FormRowView
            val result: FormRowView.Result? = rowView.recoverResult      //暂存值或显示值
            val rowStub: FormRowStub = FormRowStub(result?.rowID, result?.value)
            formStub.rowStubs.add(rowStub)
        }

        callback?.temporarySaveForm(formStub)
    }

    fun print(printID: Long) {
        checkCallbackNull()
        callback?.getFormPrintData(adapter?.form?.machineID!!, adapter?.form?.formID!!, printID, object : ReceiveCallback {
            override fun receivedFormPrintDataResult(printData: FormPrintData?) {
                if (mHasComment) {
                    for (i in 0..(childCount - 1)) {
                        val v: FormRowView = getChildAt(i) as FormRowView
                        v.disableRowView()
                        if (i == childCount - 1) {
                            v.recover(printData?.comment!!)
                        } else {
                            v.recover(printData?.printRows?.get(i)?.rowValue!!)
                        }
                    }
                } else {
                    for (i in 0..(childCount - 1)) {
                        val v: FormRowView = getChildAt(i) as FormRowView
                        v.disableRowView()
                        v.recover(printData?.printRows?.get(i)?.rowValue!!)
                    }
                }
            }

            override fun receivedFormStubResult(stub: FormStub?) {

            }

            override fun error(error: Throwable?) {
                FUtil.showToast(context, "打印数据加载失败")
            }
        })
    }

    //type1和type2分别对应istop的状态
    fun printWith(formView: FormView, printID: Long, type1: Int, type2: Int) {
        checkCallbackNull()
        callback?.getFormPrintData(adapter?.form?.machineID!!, adapter?.form?.formID!!, printID, object : ReceiveCallback {
            override fun receivedFormPrintDataResult(printData: FormPrintData?) {
                if (printData == null) {
                    return
                }
                val type1Data = FormPrintData(
                        printData.formID,
                        printData.machineID,
                        printData.workNo,
                        printData.formType,
                        printData.startTime,
                        printData.endTime,
                        printData.clsr,
                        printData.comment
                )
                val type2Data = FormPrintData(
                        printData.formID,
                        printData.machineID,
                        printData.workNo,
                        printData.formType,
                        printData.startTime,
                        printData.endTime,
                        printData.clsr,
                        printData.comment
                )
                for (row in printData.printRows) {
                    if (row.extraID.toInt() == type1) {
                        val type1Row = FormPrintRow(
                                row.rowName,
                                row.rowValue,
                                row.rowID,
                                row.extraID
                        )
                        type1Data.printRows.add(type1Row)
                    } else if (row.extraID.toInt() == type2){
                        type2Data.printRows.add(row)
                    }
                }

                printWithData(type1Data, mHasComment)
                formView.printWithData(type2Data, formView.mHasComment)
            }

            override fun receivedFormStubResult(stub: FormStub?) {

            }

            override fun error(error: Throwable?) {
                FUtil.showToast(context, "打印数据加载失败")
            }
        })
    }

    private fun printWithData(printData: FormPrintData?, hasComment: Boolean) {
        if (hasComment) {
            for (i in 0..(childCount - 1)) {
                val v: FormRowView = getChildAt(i) as FormRowView
                v.disableRowView()
                if (i == childCount - 1) {
                    v.recover(printData?.comment!!)
                } else {
                    v.recover(printData?.printRows?.get(i)?.rowValue!!)
                }
            }
        } else {
            for (i in 0..(childCount - 1)) {
                val v: FormRowView = getChildAt(i) as FormRowView
                v.disableRowView()
                v.recover(printData?.printRows?.get(i)?.rowValue!!)
            }
        }
    }

    fun recoverFormStub() {
        checkCallbackNull()
        callback?.getFormStub(adapter?.form?.machineID!!, adapter?.form?.formID!!, object : ReceiveCallback {
            override fun receivedFormPrintDataResult(printData: FormPrintData?) {

            }

            override fun receivedFormStubResult(stub: FormStub?) {
                if (mHasComment) {
                    for (i in 0..(childCount - 1)) {
                        val v: FormRowView = getChildAt(i) as FormRowView
                        if (i == childCount - 1) {
                            v.recover(stub?.comment!!)
                        } else {
                            v.recover(stub?.rowStubs?.get(i)?.itemValue!!)
                        }
                    }
                } else {
                    for (i in 0..(childCount - 1)) {
                        val v: FormRowView = getChildAt(i) as FormRowView
                        v.recover(stub?.rowStubs?.get(i)?.itemValue!!)
                    }
                }

                FUtil.showToast(context, "您正在使用暂存数据填写表单")
            }

            override fun error(error: Throwable?) {
                FUtil.showToast(context, "无暂存数据")
            }
        })
    }

    fun recoverFormStubForMerge(mergeIndex: Int) {
        checkCallbackNull()
        callback?.getFormStubForMerge(adapter?.form?.machineID!!, adapter?.form?.formID!!, mergeIndex, object : ReceiveCallback {
            override fun receivedFormPrintDataResult(printData: FormPrintData?) {

            }

            override fun receivedFormStubResult(stub: FormStub?) {
                if (mHasComment) {
                    for (i in 0..(childCount - 1)) {
                        val v: FormRowView = getChildAt(i) as FormRowView
                        if (i == childCount - 1) {
                            v.recover(stub?.comment!!)
                        } else {
                            v.recover(stub?.rowStubs?.get(i)?.itemValue!!)
                        }
                    }
                } else {
                    for (i in 0..(childCount - 1)) {
                        val v: FormRowView = getChildAt(i) as FormRowView
                        v.recover(stub?.rowStubs?.get(i)?.itemValue!!)
                    }
                }
                FUtil.showToast(context, "您正在使用暂存数据填写表单")
            }

            override fun error(error: Throwable?) {
                FUtil.showToast(context, "无暂存数据")
            }
        })
    }

    fun deleteFormStubForMerge(mergeIndex: Int) {
        checkCallbackNull()
        callback?.getFormStubForMerge(adapter?.form?.machineID!!, adapter?.form?.formID!!, mergeIndex, object : ReceiveCallback {
            override fun receivedFormPrintDataResult(printData: FormPrintData?) {

            }

            override fun receivedFormStubResult(stub: FormStub?) {
                callback?.deleteFormStubForMerge(stub)
            }

            override fun error(error: Throwable?) {

            }
        })
    }

    fun deleteFormStub() {
        checkCallbackNull()
        callback?.getFormStub(adapter?.form?.machineID!!, adapter?.form?.formID!!, object : ReceiveCallback {
            override fun receivedFormPrintDataResult(printData: FormPrintData?) {

            }

            override fun receivedFormStubResult(stub: FormStub?) {
                callback?.deleteFormStub(stub)
            }

            override fun error(error: Throwable?) {

            }
        })
    }

    fun disableFormView() {
        for (i in 0..(childCount - 1)) {
            val v: FormRowView = getChildAt(i) as FormRowView
            v.disableRowView()
        }
    }

    fun enabledFormView() {
        for (i in 0..(childCount - 1)) {
            val v: FormRowView = getChildAt(i) as FormRowView
            v.enabledRowView()
        }
    }

    public fun fillCJDefineCells(cjValues: MutableList<CJCellValue>) {
        for (value in cjValues) {
            for (i in 0..(childCount - 1)) {
                val v: FormRowView = getChildAt(i) as FormRowView
                if (v.getOrder() == value.rowOrder) {
                    v.setCJCell(value.cjItems)
                }
            }
        }
    }

    private fun checkCallbackNull() {
        if (callback == null) {
            throw Exception("form view must implement Callback interface")
        }
    }

    abstract class RowAdapter(var form: Form?) {

        fun getRowView(position: Int, formType: Int?, parent: ViewGroup, hasFormula: Boolean): View? {
            when(formType) {
                TYPE_GENERAL -> {
                    return FormRowView(parent.context).create(FormRowView.ROW_TYPE_SIMPLE, hasFormula)
                }
                TYPE_MAINTENANCE -> {
                    return FormRowView(parent.context).create(FormRowView.ROW_TYPE_MULTI_COLUMN, hasFormula)
                }
                TYPE_CJ -> {
                    return FormRowView(parent.context).create(FormRowView.ROW_TYPE_CJ, hasFormula)
                }
                TYPE_FIVE -> {
                    return null
                }
                TYPE_XJ -> {
                    return FormRowView(parent.context).create(FormRowView.ROW_TYPE_XJ, hasFormula)
                }
                else -> return null
            }
        }

        fun bindView(row: GeneralRow, contentView: View?) {
            val title: TextView? = contentView?.findViewById(R.id.rowTitle)
            title?.text = row.rowName
            val inputView: InputView? = contentView?.findViewById(R.id.input_view)
            inputView?.initView(row)
        }

        fun bindView(colRow: MultiColFormRow, contentView: View?) {
            if (contentView == null)
                return
            //设置总标题
            val title: TextView? = contentView.findViewById(R.id.rowTitle)
            if (colRow.title == "") {
                title?.visibility = View.GONE
            } else {
                title?.text = colRow.title
            }

            //设置显示列
            val linearLayout: LinearLayout? = contentView.findViewById<LinearLayout>(R.id.grid_row)
            for (item in colRow.colDisplay) {
                val view = LayoutInflater.from(contentView.context).inflate(R.layout.form_row_multi_single_display, linearLayout, false)
                val tv1 = view.findViewById<TextView>(R.id.col_title)
                val tv2 = view.findViewById<TextView>(R.id.display_value)
                tv1.text = item.dispCellName
                tv2.text = item.dispCellValue

                val lp: LinearLayout.LayoutParams = view.layoutParams as LinearLayout.LayoutParams
                lp.weight = 1f
                view.layoutParams = lp
                linearLayout?.addView(view)
            }

            //设置输入行
            val subTitle: TextView? = contentView.findViewById(R.id.sub_title)
            subTitle?.text = colRow.inputRow.rowName

            val inputView: InputView? = contentView.findViewById(R.id.input_view)
            inputView?.initMultiColView(colRow)

        }

        fun bindCJView(row: GeneralRow, contentView: View?) {
            val tvType: TextView? = contentView?.findViewById(R.id.value_type)
            val tvProject: TextView? = contentView?.findViewById(R.id.value_project)
            tvType?.text = row.rowNameExtra
            tvProject?.text = row.rowName
            val cjInputView: InputView? = contentView?.findViewById(R.id.cj_input_view)
            cjInputView?.initView(row)

            val tmp = GeneralRow("", "", "", "", "", "",
                    InputView.TYPE_MULTI_SELECT, true, "OK,NG", "", -1, "", "")
            val inputView = contentView?.findViewById<InputView>(R.id.inputView)
            inputView?.initView(tmp)
        }

        fun bindXJView(row: GeneralRow, contentView: View?) {
            val title: TextView? = contentView?.findViewById(R.id.row_title)
            if (row.rowNameExtra == "") {
                title?.visibility = View.GONE
            } else {
                title?.text = row.rowNameExtra
            }
            val inputName: TextView? = contentView?.findViewById(R.id.input_name)
            inputName?.text = row.rowName
            val inputView: InputView? = contentView?.findViewById(R.id.input_view)
            inputView?.initView(row)
        }

//        private fun inflateTxt(items: MutableList<MultiColDisplayCell>) : MutableList<MultiColDisplayCell> {
//            var maxLength = 0
//            for (item in items) {
//                if (item.dispCellValue.length > maxLength) {
//                    maxLength = item.dispCellValue.length
//                }
//            }
//
//            val tmp = MutableList<MultiColDisplayCell>(0, {index ->  MultiColDisplayCell()})
//
//            for (item in items) {
//                tmp.add(MultiColDisplayCell(item.dispCellName,
//                        item.dispCellValue + getSpace(maxLength - item.dispCellValue.length)))
//            }
//            return tmp
//        }
//
//        private fun getSpace(length: Int) : String {
//            val builder = StringBuilder()
//            for (i in 1..length) {
//                builder.append("__")
//            }
//            return builder.toString()
//        }

//        fun bindView(row: FormMergeMultiRow, contentView: View?) {
//            if (contentView == null)
//                return
//            //设置总标题
//            val title: TextView? = contentView.findViewById(R.id.title)
//            title?.text = row.title
//
//            val rowContainer = contentView.findViewById<LinearLayout>(R.id.row_container)
//            for (childRow: FormMergeMultiRow.ChildRow in row.childRows) {
//                val inflater: LayoutInflater = LayoutInflater.from(contentView.context)
//                val childRowView: View = inflater.inflate(R.layout.form_row_multi_merge_child, null)
//                //设置显示列和输入行
//                initChildRow(childRowView, childRow)
//                rowContainer.addView(childRowView)
//            }
//        }
//
//        private fun initChildRow(rowSingleView: View, row: FormMergeMultiRow.ChildRow) {
//            val gridView: GridView? = rowSingleView.findViewById(R.id.grid_row)
//            gridView?.numColumns = row.childDisplayRow.size
//            val adapter = MergeColDisplayAdapter(rowSingleView.context, row.childDisplayRow)
//            gridView?.adapter = adapter
//
//            val subTitle: TextView? = rowSingleView.findViewById(R.id.sub_title)
//            subTitle?.text = row.inputRow.rowName
//
//            val inputView: InputView? = rowSingleView.findViewById(R.id.input_view)
//            inputView?.initView(row.inputRow)
//        }
    }

    interface Callback {
        fun saveFormPrintData(stub: FormPrintData)
        fun getFormPrintData(machineID: String, formID: String, printID: Long, receiveCallback: ReceiveCallback)
        fun deleteFormPrintData(formStub: FormPrintData)

        fun temporarySaveForm(stub: FormStub)
        fun getFormStub(machineID: String, formID: String, callback: ReceiveCallback)
        fun deleteFormStub(stub: FormStub?)

        fun getFormStubForMerge(machineID: String, formID: String, mergeIndex: Int, callback: ReceiveCallback)
        fun deleteFormStubForMerge(stub: FormStub?)

        @WorkerThread fun timeDec(time: Long?)
        @WorkerThread fun timerEnd()

        fun loadFormula(formulaID: String, formulaCallback: FormulaCallback)
        fun calculateFormula(formula: String, parameters: String, formulaCallback: FormulaCallback)
    }

    interface ReceiveCallback {
        fun receivedFormPrintDataResult(printData: FormPrintData?)
        fun receivedFormStubResult(stub: FormStub?)
        fun error(error: Throwable?)
    }

    interface FormulaCallback {
        fun formulaQueryResult(formula: Formula)
        fun formulaCalculateResult(result: String)
        fun error(error: Throwable?)
    }

}