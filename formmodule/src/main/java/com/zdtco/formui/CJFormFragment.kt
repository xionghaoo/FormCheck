package com.zdtco.formui

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter

import com.xh.formlib.R
import com.zdtco.FUtil
import com.zdtco.audit.AuditActivity
import com.zdtco.datafetch.data.CJCellValue
import com.zdtco.datafetch.data.FormPostData
import com.zdtco.datafetch.data.GeneralForm
import com.zdtco.datafetch.data.GeneralRow
import kotlinx.android.synthetic.main.fragment_cjform.*
import kotlinx.android.synthetic.main.fragment_formstub_list.*
import kotlinx.android.synthetic.main.fragment_general_form.*
import kotlinx.android.synthetic.main.fragment_maintenance.*
import kotlinx.android.synthetic.main.fragment_template_four_form.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 表单注意事项:
 * 1、初件表单中初件栏位填写值不能为空，不然会提交失败
 */
class CJFormFragment : Fragment() {

    // TODO: Rename and change types of parameters
    lateinit var machineID: String
    private var formType: Int = FormView.TYPE_GENERAL
    lateinit var frontFormView : FormView
    lateinit var behindFormView : FormView

    private var mListener: OnFragmentInteractionListener? = null
    private var isPrint: Boolean = false
    private var timeLimit: Long = 0
    lateinit var adapter: ArrayAdapter<String>
    private var formNO: String? = null
    private var formID: String? = null
    private var auditPrintID: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            machineID = arguments.getString(ARG_PARAM1)
            formType = arguments.getInt(ARG_PARAM2)
            isPrint = arguments.getBoolean(ARG_IS_PRINT)
            timeLimit = arguments.getLong(ARG_TIME_LIMIT)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cjform, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()

        mListener?.loadCJForm()

        next_form.setOnClickListener { v ->
            front_form_container.visibility = View.GONE
            behind_form_container.visibility = View.VISIBLE
            val postData = frontFormView.getPostDataResult()
            for (item in postData.postRows) {
                adapter.add(item.rowName + ": " + item.rowValue)
            }
            adapter.notifyDataSetChanged()

            //填充规定栏位
            mListener?.loadDefineCellValues(formID!!, frontFormView.partNo, frontFormView.lineNo)
        }
        operation.setOnPostListener { v ->
            Log.d("postData", FormView.getPostDataForMergeString(frontFormView, behindFormView))

//            val errorMsg = behindFormView.getErrorMsg(true)
//            AlertDialog.Builder(activity)
//                    .setTitle("错误信息")
//                    .setMessage(errorMsg)
//                    .setPositiveButton("确认并提交", { dialog: DialogInterface?, which: Int ->
//                        if (errorMsg == InputView.ERROR_NONE) {
//                            mListener?.postCJFormData(FormView.getPostDataForMergeString(frontFormView, behindFormView))
//                        } else {
//                            FUtil.showToast(activity, "请修正错误后提交")
//                        }
//                    })
//                    .setNegativeButton("取消", { dialog: DialogInterface?, which: Int ->  })
//                    .show()
            mListener?.postCJFormData(FormView.getPostDataForMergeString(frontFormView, behindFormView))

        }
        operation.setOnAuditListener { v ->
            FUtil.showToast(activity, "送审")
            mListener?.saveAuditForm(formNO, formID!!, machineID, auditPrintID!!, false)
        }

        val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA)

        val list = MutableList<String>(4, { index: Int ->
            when(index) {
                0 -> "机台号: " + machineID
                1 -> "日期: " + dateFormat.format(System.currentTimeMillis())
                2 -> "提交时间: " + "e"
                3 -> "创建人: " + frontFormView.workNo
                else -> ""
            }
        })
        adapter = ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1, list)
        check_header.adapter = adapter
        check_header.numColumns = 4
    }

    private fun init() {
        frontFormView = front_form
        behindFormView = behind_form
        mListener?.setFormStubCallback(frontFormView)
        mListener?.setFormStubCallback(behindFormView)
        mListener?.initialForm(frontFormView)
        mListener?.initialForm(behindFormView)
    }

    fun loadFormFailure() {
        FUtil.showToast(activity, "加载表单失败")
        operation.visibility = View.GONE
    }

    fun showFormView(generalForm: GeneralForm) {
        formID = generalForm.id

        val frontForm = GeneralForm(generalForm.id, generalForm.name)
        val behindForm = GeneralForm(generalForm.id, generalForm.name)
        for (row in generalForm.generalRows) {
            if (row.rowExtraType == 0) {
                val frontFormRow = GeneralRow(
                        row.rowID,
                        row.rowName,
                        row.indexA,
                        row.indexB,
                        row.defaultValue,
                        row.cjRowID,
                        row.inputType,
                        row.isMustWrite,
                        row.initValue,
                        row.rowNameExtra,
                        row.rowExtraType,
                        row.formulaExecuteRow,
                        row.formulaParameterRows
                )
                frontForm.generalRows.add(frontFormRow)
            } else {
                behindForm.generalRows.add(row)
            }
        }

        val formF = Form.Builder()
                .setName(generalForm.id)
                .setMachineID(machineID)
                .setFormType(FormView.TYPE_GENERAL)
                .isStub(false)
                .setTimeLimit(0)
                .generalForm(frontForm)
                .build()

        frontFormView.setAdapter(FormRowAdapter(formF), false)

        val formB = Form.Builder()
                .setName(generalForm.id)
                .setMachineID(machineID)
                .setFormType(FormView.TYPE_CJ)
                .isStub(false)
                .setTimeLimit(timeLimit)
                .generalForm(behindForm)
                .build()

        behindFormView.setAdapter(FormRowAdapter(formB), true)

        if (isPrint) {
            front_form_container.visibility = View.GONE
            behind_form_container.visibility = View.VISIBLE
            frontFormView.printWith(behindFormView, mListener?.printID()!!, 0, 1)
            operation.visibility = View.GONE

            Timer().schedule(object : TimerTask() {
                override fun run() {
                    activity.runOnUiThread {
                        val postData = frontFormView.getPostDataResult()
                        for (item in postData.postRows) {
                            adapter.add(item.rowName + ": " + item.rowValue)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }, 1000)
        } else {
            frontFormView.recoverFormStubForMerge(1)
            behindFormView.recoverFormStubForMerge(2)
        }
    }

    fun showFormDescriptionView(formID: String?, desc: String?) {
        if (formID == null || desc == null) {
            return
        }
        operation.showFormDescription(formID, desc)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    fun cancelTimeTask() {
        behindFormView.cancelTimeTask()
    }

    fun savePostForm(success: Boolean, formNo: String?) {
        if (success) {
            formNO = formNo
        }
        frontFormView.saveFormWith(behindFormView, success)
    }

    fun saveAuditRecord(printID: Long) {
        //点击提交按钮后这个方法会自动调用
        if (formNO != null) {
            mListener?.saveAuditForm(formNO, formID!!, machineID, printID, true)
            auditPrintID = printID
            operation.showAuditButton()
            operation.hidePostButton()
        }
    }

    fun temporarySaveForm() {
        frontFormView.temporarySaveMerge(1)
        behindFormView.temporarySaveMerge(2)
    }

    fun deleteFormStubRecord() {
        frontFormView.deleteFormStubForMerge(1)
        behindFormView.deleteFormStubForMerge(2)
    }

    fun fillDefineCells(cjCells: MutableList<CJCellValue>) {
        behindFormView.fillCJDefineCells(cjCells)
    }

    class FormRowAdapter(from: Form) : FormView.RowAdapter(from) {

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun setFormStubCallback(formView: FormView)
        fun loadCJForm()
        fun initialForm(formView: FormView)
        fun postCJFormData(form: String)

        fun printID() : Long

        fun saveAuditForm(formNo: String?, formID: String, machineID: String, printID: Long, isSave: Boolean)

        fun loadDefineCellValues(formID: String, partNo: String, lineNo: String)
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"
        private val ARG_IS_PRINT = "is_print"
        private val ARG_TIME_LIMIT = "time_limit"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CJFormFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: Int, isPrint: Boolean, time: Long): CJFormFragment {
            val fragment = CJFormFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putInt(ARG_PARAM2, param2)
            args.putBoolean(ARG_IS_PRINT, isPrint)
            args.putLong(ARG_TIME_LIMIT, time)
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
