package com.zdtco.formui

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.xh.formlib.R
import com.zdtco.FUtil
import com.zdtco.datafetch.data.FormCheckStatus
import com.zdtco.datafetch.data.GeneralForm
import com.zdtco.datafetch.data.GeneralRow
import kotlinx.android.synthetic.main.fragment_template_four_form.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [TemplateFourFormFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [TemplateFourFormFragment.newInstance] factory method to
 * create an instance of this fragment.
 * 生产日报表，controltype为空的为无效栏位，istop为1的点击开始之前显示，为2的点击开始之后的显示
 */
class TemplateFourFormFragment : Fragment() {

    lateinit var machineID: String
//    lateinit var formID: String
    private var formType: Int = FormView.TYPE_GENERAL
    private var isPrint: Boolean = false
    lateinit var status: FormCheckStatus

    private var mListener: OnFragmentInteractionListener? = null
    private var timeLimit: Long = 0

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
        return inflater.inflate(R.layout.fragment_template_four_form, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mListener?.loadTemplateFourForm()

        mListener?.setFormStubCallback(form1)
        mListener?.setFormStubCallback(form2)
        mListener?.initialForm(form1)
        mListener?.initialForm(form2)

        if (isPrint) {
            start_check.visibility = View.GONE
            template_four_operation.visibility = View.GONE
        }

        start_check.setOnClickListener { v: View? ->
            status.isCheckedFirstForm = true
            mListener?.saveTemplateFourFormCheckStatus(status)
            form1.temporarySaveMerge(1)
            startedCheck()
        }

        template_four_operation.setOnPostListener {v: View? ->
            //提交表单
            Log.d("test", "result = " + FormView.getPostDataForMergeString(form1, form2))
            mListener?.postTemplateFourForm(FormView.getPostDataForMergeString(form1, form2))
        }
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

    fun loadFormFailure() {
        FUtil.showToast(activity, "加载表单失败")
        template_four_operation.visibility = View.GONE
    }

    fun showTemplateFourFormView(generalForm: GeneralForm) {
//        formID = generalForm.id
        status = FormCheckStatus(machineID, generalForm.id)

        val form1Data = GeneralForm(generalForm.id, generalForm.name)
        val form2Data = GeneralForm(generalForm.id, generalForm.name)
        for (row in generalForm.generalRows) {
            if (row.rowExtraType == 1) {
                //开始之前填写
                val form1Row = GeneralRow(
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
                form1Data.generalRows.add(form1Row)
            } else if (row.rowExtraType == 2) {
                form2Data.generalRows.add(row)
            }
        }

        val formF = Form.Builder()
                .setName(generalForm.id)
                .setMachineID(machineID)
                .setFormType(FormView.TYPE_GENERAL)
                .isStub(false)
                .setTimeLimit(0)
                .generalForm(form1Data)
                .build()

        form1.setAdapter(FormRowAdapter(formF), false)

        val formB = Form.Builder()
                .setName(generalForm.id)
                .setMachineID(machineID)
                .setFormType(FormView.TYPE_GENERAL)
                .isStub(false)
                .setTimeLimit(timeLimit)
                .generalForm(form2Data)
                .build()

        form2.setAdapter(FormRowAdapter(formB), true)

        if (isPrint) {
            form1.printWith(form2, mListener?.printID()!!, 1, 2)
        } else {
            mListener?.loadTemplateFourFormCheckStatus()

            form1.recoverFormStubForMerge(1)
            form2.recoverFormStubForMerge(2)
        }

    }

    private fun startedCheck() {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
        form1.checkStartTime = dateFormat.format(System.currentTimeMillis())

        form1.disableFormView()
        form2.enabledFormView()
        template_four_operation.enabledView()
        start_check.isEnabled = false
    }

    fun setFormStatus(status: FormCheckStatus) {
        if (status.isCheckedFirstForm) {
//            form1.recoverFormStubForMerge(1)
            form1.recoverFormStubForMerge(1)
            startedCheck()
        } else {
            form2.disableFormView()
            template_four_operation.disableView()
        }
    }

    fun cancelTimeTask() {
        form2.cancelTimeTask()
    }

    fun savePostForm(success: Boolean) {
        mListener?.deleteTemplateFourFormCheckStatus(status)
        form1.saveFormWith(form2, success)
    }

    fun temporarySaveForm() {
        form1.temporarySaveMerge(1)
        form2.temporarySaveMerge(2)
    }

    fun deleteFormStubRecord() {
        form1.deleteFormStubForMerge(1)
        form2.deleteFormStubForMerge(2)
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
        fun initialForm(formView: FormView)
        fun loadTemplateFourForm()
        fun postTemplateFourForm(form: String)

        fun loadTemplateFourFormCheckStatus()
        fun saveTemplateFourFormCheckStatus(status: FormCheckStatus)
        fun deleteTemplateFourFormCheckStatus(status: FormCheckStatus)

        fun printID() : Long

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
         * @return A new instance of fragment TemplateFourFormFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: Int, isPrint: Boolean, time: Long): TemplateFourFormFragment {
            val fragment = TemplateFourFormFragment()
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
