package com.zdtco.formui

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.xh.formlib.R
import com.zdtco.FUtil
import com.zdtco.datafetch.data.MultiColumnForm
import kotlinx.android.synthetic.main.fragment_general_form.*
import kotlinx.android.synthetic.main.fragment_maintenance.*

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MaintenanceFormFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MaintenanceFormFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MaintenanceFormFragment : Fragment() {
    private var isTestForm: Boolean = false

    // TODO: Rename and change types of parameters
    lateinit var machineID: String
    private var formType: Int = FormView.TYPE_GENERAL

    private var mListener: OnFragmentInteractionListener? = null
    private var isPrint: Boolean = false
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
        return inflater.inflate(R.layout.fragment_maintenance, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mListener?.setFormStubCallback(maintenance_form)
        mListener?.initialForm(maintenance_form)
        mListener?.loadMaintenanceForm()

        if (isPrint) {
            maintenance_form_operation.visibility = View.GONE
        }

        maintenance_form_operation.setOnPostListener { v ->
            Log.d("test", "result = " + maintenance_form.getPostStringResults())
            val errorMsg = maintenance_form.getErrorMsg(true)
            AlertDialog.Builder(activity)
                    .setTitle("错误信息")
                    .setMessage(errorMsg)
                    .setPositiveButton("确认并提交", { dialog: DialogInterface?, which: Int ->
//                        mListener?.postMaintenanceFormData(maintenance_form.getPostStringResults())

                        //如果输入中有错误信息，需要修正以后才能提交
                        if (errorMsg == InputView.ERROR_NONE) {
                            mListener?.postMaintenanceFormData(maintenance_form.getPostStringResults())
                        } else {
                            if (isTestForm) {
                                if (errorMsg.contains("项目为空".toRegex())) {
                                    FUtil.showToast(activity, "填写项中有空值，请填写完成后提交")
                                } else {
                                    mListener?.postMaintenanceFormData(maintenance_form.getPostStringResults())
                                }
                            } else {
                                FUtil.showToast(activity, "请修正错误后提交")
                            }
//                            if (errorMsg.contains("项目为空".toRegex())) {
//                                FUtil.showToast(activity, "填写项中有空值，请填写完成后提交")
//                            } else {
//                                mListener?.postMaintenanceFormData(maintenance_form.getPostStringResults())
//                            }
//                            FUtil.showToast(activity, "请修正错误后提交")
                        }
                    })
                    .setNegativeButton("取消", { dialog: DialogInterface?, which: Int ->  })
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

    fun showFormDescriptionView(formID: String?, desc: String?) {
        if (formID == null || desc == null) {
            return
        }
        maintenance_form_operation.showFormDescription(formID, desc)
    }

    fun loadFormFailure() {
        FUtil.showToast(activity, "加载表单失败")
        maintenance_form_operation.visibility = View.GONE
    }

    fun showFormView(multiColumnForm: MultiColumnForm) {
        isTestForm = multiColumnForm.isTestForm

        val f = Form.Builder()
                .setName(multiColumnForm.formID)
                .setMachineID(machineID)
                .setFormType(formType)
                .isStub(false)
                .setTimeLimit(timeLimit)
                .multiColumnForm(multiColumnForm)
                .build()
        maintenance_form.setAdapter(FormRowAdapter(f), true)

        if (isPrint) {
            maintenance_form.print(mListener?.printID()!!)
        } else {
            maintenance_form.recoverFormStub()
        }
    }

    fun savePostForm(success: Boolean) {
        maintenance_form.saveForm(success)
    }

    fun temporarySaveForm() {
        maintenance_form.temporarySave()
    }

    fun deleteFormStubRecord() {
        maintenance_form.deleteFormStub()
    }

    fun cancelTimeTask() {
        maintenance_form.cancelTimeTask()
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
        fun loadMaintenanceForm()
        fun initialForm(formView: FormView)
        fun postMaintenanceFormData(form: String)
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
         * @return A new instance of fragment MaintenanceFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: Int, isPrint: Boolean, time: Long): MaintenanceFormFragment {
            val fragment = MaintenanceFormFragment()
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
