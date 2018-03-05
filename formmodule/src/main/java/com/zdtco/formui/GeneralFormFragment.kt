package com.zdtco.formui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.xh.formlib.R
import com.zdtco.FUtil
import com.zdtco.datafetch.data.GeneralForm
import kotlinx.android.synthetic.main.fragment_general_form.*
import kotlinx.android.synthetic.main.fragment_maintenance.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [GeneralFormFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [GeneralFormFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GeneralFormFragment : Fragment() {
    private var isTestForm: Boolean = false

    lateinit var machineID: String
    private var formType: Int = FormView.TYPE_GENERAL
    lateinit var formView: FormView
    private var isPrint: Boolean = false
    private var timeLimit: Long = 0

    private var mListener: OnFragmentInteractionListener? = null

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
        val v: View = inflater.inflate(R.layout.fragment_general_form, container, false)
        return v
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("GeneralFormFragment", "load GeneralFormFragment")
        formView = general_form
        mListener?.setFormStubCallback(formView)
        mListener?.initialForm(formView)
        mListener?.loadGeneralForm()

        if (isPrint) {
            general_form_operation.visibility = View.GONE
        }

        general_form_operation.setOnPostListener { v ->
            Log.d("GeneralFormFragment", "result = " + formView.getPostStringResults())
            val errorMsg = formView.getErrorMsg(true)
            AlertDialog.Builder(activity)
                    .setTitle("错误信息")
                    .setMessage(errorMsg)
                    .setPositiveButton("确认并提交", {dialog: DialogInterface?, which: Int ->
//                        mListener?.postGeneralFormData(formView.getPostStringResults())

                        //如果输入中有错误信息，需要修正以后才能提交
                        if (errorMsg == InputView.ERROR_NONE) {
                            mListener?.postGeneralFormData(formView.getPostStringResults())
                        } else {
                            if (isTestForm) {
                                if (errorMsg.contains("项目为空".toRegex())) {
                                    FUtil.showToast(activity, "填写项中有空值，请填写完成后提交")
                                } else {
                                    mListener?.postGeneralFormData(formView.getPostStringResults())
                                }
                            } else {
                                FUtil.showToast(activity, "请修正错误后提交")
                            }
                        }
                    })
                    .setNegativeButton("取消", {dialog: DialogInterface?, which: Int ->  })
                    .show()
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
        general_form_operation.visibility = View.GONE
    }

    fun showFormView(generalForm: GeneralForm) {
        isTestForm = generalForm.isTestPostForm

        val form = Form.Builder()
                .setName(generalForm.id)
                .setMachineID(machineID)
                .setFormType(formType)
                .isStub(false)
                .setTimeLimit(timeLimit)
                .generalForm(generalForm)
                .build()

        formView.setAdapter(FormRowAdapter(form), true)
        if (isPrint) {
            formView.print(mListener?.printID()!!)
        } else {
            formView.recoverFormStub()
        }

    }

    fun savePostForm(success: Boolean) {
        formView.saveForm(success)
    }

    fun temporarySaveForm() {
        formView.temporarySave()
    }

    fun deleteFormStubRecord() {
        formView.deleteFormStub()
    }

    fun cancelTimeTask() {
        formView.cancelTimeTask()
    }

    class FormRowAdapter(from: Form) : FormView.RowAdapter(from) {

    }

    interface OnFragmentInteractionListener {
        fun setFormStubCallback(formView: FormView)
        fun loadGeneralForm()
        fun initialForm(formView: FormView)
        fun postGeneralFormData(form: String)
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
         * @return A new instance of fragment GeneralFormFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: Int, isPrint: Boolean, time: Long): GeneralFormFragment {
            val fragment = GeneralFormFragment()
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
