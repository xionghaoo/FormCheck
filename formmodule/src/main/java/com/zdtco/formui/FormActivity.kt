package com.zdtco.formui

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.annotation.WorkerThread
import android.support.v7.app.AlertDialog
import android.util.Log
import android.util.Pair
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import com.xh.formlib.R
import com.zdtco.BaseActivity
import com.zdtco.BaseApplication
import com.zdtco.FUtil
import com.zdtco.audit.AuditActivity
import com.zdtco.datafetch.Repository
import com.zdtco.datafetch.data.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_form.*

public class FormActivity : BaseActivity(),
        GeneralFormFragment.OnFragmentInteractionListener,
        CJFormFragment.OnFragmentInteractionListener,
        MaintenanceFormFragment.OnFragmentInteractionListener,
        TemplateFourFormFragment.OnFragmentInteractionListener,
        TemplateFiveFormFragment.OnFragmentInteractionListener,
        TemplateFiveItemFragment.OnListFragmentInteractionListener
{

    private val TAG: String? = "FormActivity"
    lateinit var machineOwnedForm: MachineOwnedForm
    lateinit var workNo: String
    private var isPrint: Boolean = false
    private var printDataID: Long = 0
    private var cjUploadFailureCount: Int = 0;

    companion object {
        var EXTRA_FORM: String = "FormActivity.extra_form"
        var EXTRA_WORKNO: String = "FormActivity.extra_work_no"
        var EXTRA_FORM_PRINT_ID: String = "FormActivity.extra_print_id"
        var EXTRA_IS_PRINT: String = "FormActivity.extra_is_print"
        val TAG_FRAG_CJ: String = "tag_frag_cj"
        val TAG_FRAG_GENERAL: String = "tag_frag_general"
        val TAG_FRAG_MAINTENANCE: String = "tag_frag_maintenance"
        val TAG_FRAG_TEMPLATE_FOUR: String = "tag_frag_template_four"
        val TAG_FRAG_TEMPLATE_FIVE: String = "tag_frag_template_five"
        val TAG_FRAG_TEMPLATE_FIVE_ITEM: String = "tag_frag_template_five_item"

        val PROGRESS_MSG: String = "正在上传..."
    }

    private var repo: Repository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        repo = (application as BaseApplication).repository

        progressDialog = FUtil.progressDialog(this, PROGRESS_MSG, false)

        machineOwnedForm = intent.getParcelableExtra(EXTRA_FORM)
        workNo = intent.getStringExtra(EXTRA_WORKNO)
        isPrint = intent.getBooleanExtra(EXTRA_IS_PRINT, false)
        printDataID = intent.getLongExtra(EXTRA_FORM_PRINT_ID, 0)

        title = machineOwnedForm.formName + " (设备" + machineOwnedForm.refId + ")"

//        machineOwnedForm.timeLimit = 60

        if (machineOwnedForm.timeLimit > 0) {
            time_limit.visibility = View.VISIBLE
        } else {
            time_limit.visibility = View.GONE
        }

        val fragTransaction = supportFragmentManager.beginTransaction()
        when(machineOwnedForm.templateType) {
            FormView.TYPE_GENERAL -> fragTransaction.add(R.id.frag_container,
                    GeneralFormFragment.newInstance(machineOwnedForm.refId, machineOwnedForm.templateType, isPrint, machineOwnedForm.timeLimit), TAG_FRAG_GENERAL)
            FormView.TYPE_CJ -> {
                fragTransaction.add(R.id.frag_container,
                    CJFormFragment.newInstance(machineOwnedForm.refId, machineOwnedForm.templateType, isPrint, machineOwnedForm.timeLimit), TAG_FRAG_CJ)
            }
            FormView.TYPE_MAINTENANCE -> fragTransaction.add(R.id.frag_container,
                    MaintenanceFormFragment.newInstance(machineOwnedForm.refId, machineOwnedForm.templateType, isPrint, machineOwnedForm.timeLimit), TAG_FRAG_MAINTENANCE)
            FormView.TYPE_FOUR -> fragTransaction.add(R.id.frag_container,
                    TemplateFourFormFragment.newInstance(machineOwnedForm.refId, machineOwnedForm.templateType, isPrint, machineOwnedForm.timeLimit), TAG_FRAG_TEMPLATE_FOUR)
            FormView.TYPE_FIVE -> {
                fragTransaction.add(R.id.frag_container,
                        TemplateFiveFormFragment.newInstance(machineOwnedForm.refId, machineOwnedForm.templateType, isPrint, machineOwnedForm.timeLimit), TAG_FRAG_TEMPLATE_FIVE)
            }
            FormView.TYPE_HC -> {
                fragTransaction.add(R.id.frag_container, TemplateHCFragment())
            }
            else -> {
                fragTransaction.add(R.id.frag_container,
                        GeneralFormFragment.newInstance(machineOwnedForm.refId, machineOwnedForm.templateType, isPrint, machineOwnedForm.timeLimit), TAG_FRAG_GENERAL)
            }
        }
        fragTransaction.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_form, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.temporary_save) {
            if (isPrint) {
                return true
            }
            when(machineOwnedForm.templateType) {
                FormView.TYPE_GENERAL -> {
                    FUtil.showToast(this, "暂存功能不可用")
                }
                FormView.TYPE_XJ -> {
                    val frag: GeneralFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_GENERAL) as GeneralFormFragment
                    frag.temporarySaveForm()
                    FUtil.showToast(this, "表单已暂存")
                }
                FormView.TYPE_CJ -> {
                    val frag: CJFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_CJ) as CJFormFragment
                    frag.temporarySaveForm()
                    FUtil.showToast(this, "表单已暂存")
                }
                FormView.TYPE_MAINTENANCE -> {
                    val frag: MaintenanceFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_MAINTENANCE) as MaintenanceFormFragment
                    frag.temporarySaveForm()
                    FUtil.showToast(this, "表单已暂存")
                }
                FormView.TYPE_FOUR -> {
                    val frag: TemplateFourFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FOUR) as TemplateFourFormFragment
                    frag.temporarySaveForm()
                    FUtil.showToast(this, "表单已暂存")
                }
                FormView.TYPE_FIVE -> {
                    val frag: TemplateFiveFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FIVE) as TemplateFiveFormFragment
                    frag.temporarySaveForm()
                    FUtil.showToast(this, "表单已暂存")
                }
                else -> {
                    FUtil.showToast(this, "该表单模板无法暂存")
                }
            }
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        when(machineOwnedForm.templateType) {
            FormView.TYPE_GENERAL, FormView.TYPE_XJ -> {
                val frag: GeneralFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_GENERAL) as GeneralFormFragment
                frag.cancelTimeTask()
            }
            FormView.TYPE_CJ -> {
                val frag: CJFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_CJ) as CJFormFragment
                frag.cancelTimeTask()
            }
            FormView.TYPE_MAINTENANCE -> {
                val frag: MaintenanceFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_MAINTENANCE) as MaintenanceFormFragment
                frag.cancelTimeTask()
            }
            FormView.TYPE_FOUR -> {
                val frag: TemplateFourFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FOUR) as TemplateFourFormFragment
                frag.cancelTimeTask()
            }
            FormView.TYPE_FIVE -> {
                val frag: TemplateFiveFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FIVE) as TemplateFiveFormFragment
                frag.cancelTimeTask()
            }
            else -> {
            }
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (!isPrint) {
            AlertDialog.Builder(this)
                    .setTitle("表单未提交，是否退出？")
                    .setPositiveButton("是", {dialog, which ->
                        super.onBackPressed()
                    })
                    .setNegativeButton("否", {dialog, which ->

                    })
                    .show()
        } else {
            super.onBackPressed()
        }

//        when(machineOwnedForm.templateType) {
//            FormView.TYPE_GENERAL, FormView.TYPE_XJ -> {
//                val frag: GeneralFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_GENERAL) as GeneralFormFragment
//                super.onBackPressed()
//            }
//            FormView.TYPE_CJ -> {
//                val frag: CJFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_CJ) as CJFormFragment
//                super.onBackPressed()
//            }
//            FormView.TYPE_MAINTENANCE -> {
//                val frag: MaintenanceFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_MAINTENANCE) as MaintenanceFormFragment
//                super.onBackPressed()
//            }
//            FormView.TYPE_FOUR -> {
//                val frag: TemplateFourFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FOUR) as TemplateFourFormFragment
//                super.onBackPressed()
//            }
//            FormView.TYPE_FIVE -> {
//                val frag: TemplateFiveFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FIVE) as TemplateFiveFormFragment
//                super.onBackPressed()
//            }
//            else -> {
//                super.onBackPressed()
//            }
//        }
    }

    override fun setFormStubCallback(formView: FormView) {
        setFormCallback(formView)
    }

    private fun setFormCallback(formView: FormView) {
        formView.setCallback(object : FormView.Callback {
            override fun saveFormPrintData(stub: FormPrintData) {
                repo?.saveFormPrintData(stub)
                        ?.subscribeOn(Schedulers.io())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe({ index: Long ->
                            Log.d("test", "insert index = " + index)
                            if (machineOwnedForm.templateType == FormView.TYPE_CJ) {
                                val frag: CJFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_CJ) as CJFormFragment
                                frag.saveAuditRecord(index)
                            }
                        }, {t: Throwable? ->

                        })
            }

            override fun getFormPrintData(machineID: String, formID: String, printID: Long, receiveCallback: FormView.ReceiveCallback) {
                repo?.getFormPrintData(Pair.create(machineID, formID), printID)
                        ?.subscribeOn(Schedulers.io())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe({printData: FormPrintData? ->
                            receiveCallback.receivedFormPrintDataResult(printData)
                        }, {error: Throwable? ->
                            receiveCallback.error(error)
                            Log.e("test", "error: " + error?.localizedMessage)
                        })
            }

            override fun deleteFormPrintData(formStub : FormPrintData) {
                repo?.deleteFormPrintData(formStub)
            }

            override fun temporarySaveForm(stub: FormStub) {
                repo?.temporarySaveFormRecord(stub)
            }

            override fun getFormStub(machineID: String, formID: String, callback: FormView.ReceiveCallback) {
                repo?.getFormStubRecord(Pair.create(machineID, formID))
                        ?.subscribeOn(Schedulers.io())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe({ stub: FormStub? ->
                            callback.receivedFormStubResult(stub)
                        }, { t: Throwable? ->
                            callback.error(t)
                        })
            }

            override fun deleteFormStub(stub: FormStub?) {
                repo?.deleteFormStub(stub)
            }

            override fun getFormStubForMerge(machineID: String, formID: String, mergeIndex: Int, callback: FormView.ReceiveCallback) {
                repo?.getFormStubRecordForMerge(Pair.create(machineID, formID), mergeIndex)
                        ?.subscribeOn(Schedulers.io())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe({ stub: FormStub? ->
                            callback.receivedFormStubResult(stub)
                        }, { t: Throwable? ->
                            callback.error(t)
                        })
            }

            override fun deleteFormStubForMerge(stub: FormStub?) {
                repo?.deleteFormStubForMerge(stub)
            }

            @WorkerThread
            override fun timeDec(time: Long?) {
                runOnUiThread {
                    time_limit.text = "剩余填写时间: " + time
                }
            }

            @WorkerThread
            override fun timerEnd() {
                runOnUiThread {finish()}
            }

            override fun loadFormula(formulaID: String, formulaCallback: FormView.FormulaCallback) {
                repo?.getFormula(formulaID)
                        ?.subscribeOn(Schedulers.io())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.subscribe({t: Formula? ->
                            formulaCallback.formulaQueryResult(t!!)
                        }, {t: Throwable? ->
                            formulaCallback.error(t)
                        })
            }

            override fun calculateFormula(formula: String, parameters: String, formulaCallback: FormView.FormulaCallback) {
                progressDialog.setMessage("正在计算...")
                progressDialog.show()
                repo?.executeFormula(formula, parameters)
                        ?.subscribeOn(Schedulers.io())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.doFinally {
                            progressDialog.hide()
                            progressDialog.setMessage(PROGRESS_MSG)
                        }
                        ?.subscribe({result: String? ->
                            formulaCallback.formulaCalculateResult(result!!)
                        }, {t: Throwable? ->
                            formulaCallback.error(t)
                        })
            }
        })
    }

    override fun initialForm(formView: FormView) {
        formView.initialForm(workNo,
                machineOwnedForm.refId,
                machineOwnedForm.templateType.toString())
    }

    override fun postGeneralFormData(form: String) {
        progressDialog.show()
        Log.d("postData", form)
        repo?.postFormData(form)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally { progressDialog.hide() }
                ?.subscribe({ success: Boolean? ->
                    val frag: GeneralFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_GENERAL) as GeneralFormFragment
                    if (success!!) {
                        FUtil.showToast(this, "上传成功")
                        frag.savePostForm(true)
                    } else {
                        FUtil.showToast(this, "上传失败, 表单已保存至未上传列表")
                        frag.savePostForm(false)
                    }
                    //提交后删掉表单的暂存记录
                    frag.deleteFormStubRecord()
                    finish()
                }, {t: Throwable? ->
                    FUtil.showToast(this, "上传失败, 表单已保存至未上传列表: " + t?.localizedMessage)
                    val frag: GeneralFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_GENERAL) as GeneralFormFragment
                    frag.savePostForm(false)
                    //提交后删掉表单的暂存记录
                    frag.deleteFormStubRecord()
                    finish()
                })
    }

    override fun postMaintenanceFormData(form: String) {
        progressDialog.show()
        repo?.postFormData(form)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally { progressDialog.hide() }
                ?.subscribe({ success: Boolean? ->
                    val frag: MaintenanceFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_MAINTENANCE) as MaintenanceFormFragment
                    if (success!!) {
                        FUtil.showToast(this, "上传成功")
                        frag.savePostForm(true)
                    } else {
                        FUtil.showToast(this, "上传失败, 表单已保存至未上传列表")
                        frag.savePostForm(false)
                    }
                    //提交后删掉表单的暂存记录
                    frag.deleteFormStubRecord()
                    finish()
                }, {t: Throwable? ->
                    FUtil.showToast(this, "上传失败, 表单已保存至未上传列表: " + t?.localizedMessage)
                    Log.e("test", t?.localizedMessage)
                    val frag: MaintenanceFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_MAINTENANCE) as MaintenanceFormFragment
                    frag.savePostForm(false)
                    //提交后删掉表单的暂存记录
                    frag.deleteFormStubRecord()
                    finish()
                })
    }

    override fun postCJFormData(form: String) {
        if (machineOwnedForm.macType == 4) {
            //一张初件表绑定多台机，一次提交多台机对应的初件表
            repo?.loadMachinesByForm(machineOwnedForm.formID)
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({machineIDs: MutableList<String>? ->
                        if (machineIDs == null) return@subscribe
                        val idList = ArrayList<String>()
                        val selectItems = ArrayList<SelectBoundMachineItem>()
                        for (id: String in machineIDs) {
                            idList.add(form.replace("\"Equ_id\":\"[\\w^\\W]+\"".toRegex(), "\"Equ_id\":\"" + id + "\""))
                            selectItems.add(SelectBoundMachineItem(id))
                        }
                        //显示可提交的机台列表
                        val listView = layoutInflater.inflate(R.layout.dialog_select_list, null) as ListView
                        val bdAdapter = BoundMachineAdapter(this@FormActivity, selectItems)
                        listView.adapter = bdAdapter
                        AlertDialog.Builder(this@FormActivity)
                                .setView(listView)
                                .setTitle("选择需要提交的设备")
                                .setPositiveButton("确定", {dialog: DialogInterface?, which: Int ->
                                    progressDialog.show()
                                    cjUploadFailureCount = 0
                                    postSingleCJFormRecursion(bdAdapter.getMachineIDList(), 0)
                                })
                                .setNegativeButton("取消", {dialog: DialogInterface?, which: Int ->

                                })
                                .show()
                    }, { t: Throwable? ->
                        FUtil.showToast(this@FormActivity, "提交失败: " + t?.localizedMessage)
                    })
        } else {
            progressDialog.show()
            postSingleCJForm(form)
        }
    }

    private fun postSingleCJForm(form: String) {
        repo?.postCJFormData(form)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally { progressDialog.hide() }
                ?.subscribe({ paperNo: FormPaperNo? ->
                    val frag: CJFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_CJ) as CJFormFragment
                    if (paperNo?.isSuccess!!) {
                        FUtil.showToast(this, "上传成功")
                        frag.savePostForm(true, paperNo.paperNo)
                    } else {
                        FUtil.showToast(this, "上传失败, 表单已保存至未上传列表")
                        frag.savePostForm(false, null)
                    }
                    //提交后删掉表单的暂存记录
                    frag.deleteFormStubRecord()
//                    finish()
                }, {t: Throwable? ->
                    FUtil.showToast(this, "上传失败, 表单已保存至未上传列表: " + t?.localizedMessage)
                    val frag: CJFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_CJ) as CJFormFragment
                    frag.savePostForm(false, null)
                    //提交后删掉表单的暂存记录
                    frag.deleteFormStubRecord()
//                    finish()
                })
    }

    private fun postSingleCJFormRecursion(idList: List<String>, index: Int) {
        if (index >= idList.size) {
            progressDialog.hide()
            FUtil.showToast(this, "上传成功 " + (idList.size - cjUploadFailureCount) + " 笔， 上传失败 " + cjUploadFailureCount + " 笔")
            return
        }

        val machineID = idList[index]

        repo?.postCJFormData(machineID)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({ paperNo: FormPaperNo? ->
                    val frag: CJFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_CJ) as CJFormFragment
                    if (paperNo?.isSuccess!!) {
                        if (machineID == machineOwnedForm.refId) {
                            frag.savePostForm(true, paperNo.paperNo)
                        }
                    } else {
//                        FUtil.showToast(this, "上传失败, 表单已保存至未上传列表")
                        if (machineID == machineOwnedForm.refId) {
                            frag.savePostForm(false, null)
                        }

                        cjUploadFailureCount ++
                    }
                    //提交后删掉表单的暂存记录
                    if (machineID == machineOwnedForm.refId) {
                        frag.deleteFormStubRecord()
                    }
//                    finish()

                    postSingleCJFormRecursion(idList, index + 1)
                }, {t: Throwable? ->
//                    FUtil.showToast(this, "上传失败, 表单已保存至未上传列表: " + t?.localizedMessage)
                    if (machineID == machineOwnedForm.refId) {
                        val frag: CJFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_CJ) as CJFormFragment
                        frag.savePostForm(false, null)
                        frag.deleteFormStubRecord()
                    }
                    cjUploadFailureCount ++
//                    finish()
                })
    }

    override fun postTemplateFourForm(form: String) {
        progressDialog.show()
        Log.d("postData", form)
        repo?.postFormData(form)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally { progressDialog.hide() }
                ?.subscribe({ success: Boolean? ->
                    val frag: TemplateFourFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FOUR) as TemplateFourFormFragment
                    if (success!!) {
                        FUtil.showToast(this, "上传成功")
                        frag.savePostForm(true)
                    } else {
                        FUtil.showToast(this, "上传失败, 表单已保存至未上传列表")
                        frag.savePostForm(false)
                    }
                    //提交后删掉表单的暂存记录
                    frag.deleteFormStubRecord()
                    finish()
                }, {t: Throwable? ->
                    FUtil.showToast(this, "上传失败, 表单已保存至未上传列表: " + t?.localizedMessage)
                    val frag: TemplateFourFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FOUR) as TemplateFourFormFragment
                    frag.savePostForm(false)
                    //提交后删掉表单的暂存记录
                    frag.deleteFormStubRecord()
                    finish()
                })
    }

    override fun postTemplateFiveForm(form: String) {
        progressDialog.show()
        Log.d("postData", form)
        repo?.postFormData(form)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally { progressDialog.hide() }
                ?.subscribe({ success: Boolean? ->
                    val frag: TemplateFiveFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FIVE) as TemplateFiveFormFragment
                    if (success!!) {
                        FUtil.showToast(this, "上传成功")
                        frag.savePostForm(true)
                    } else {
                        FUtil.showToast(this, "上传失败, 表单已保存至未上传列表")
                        frag.savePostForm(false)
                    }
                    //提交后删掉表单的暂存记录
                    frag.deleteFormStubRecord()
                    finish()
                }, {t: Throwable? ->
                    FUtil.showToast(this, "上传失败, 表单已保存至未上传列表: " + t?.localizedMessage)
                    val frag: TemplateFiveFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FIVE) as TemplateFiveFormFragment
                    frag.savePostForm(false)
                    //提交后删掉表单的暂存记录
                    frag.deleteFormStubRecord()
                    finish()
                })
    }

    //----------------------------------------初件表单-------------------------------------
    override fun loadCJForm() {
        progressDialog.setMessage("正在加载...")
        progressDialog.show()
        repo?.getGeneralFromLocal(machineOwnedForm.formID)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally {
                    progressDialog.hide()
                    //恢复progress dialog文本
                    progressDialog.setMessage("正在上传...")
                }
                ?.subscribe(
                        {generalForm: GeneralForm? ->
                            val frag: CJFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_CJ) as CJFormFragment
                            frag.showFormView(generalForm!!) },
                        {error: Throwable? ->
                            val frag: CJFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_CJ) as CJFormFragment
                            frag.loadFormFailure()}
                )
    }

    override fun saveAuditForm(formNo: String?, formID: String, machineID: String, printID: Long, isSave: Boolean) {
        val auditForm = AuditForm(formNo!!, machineID, formID, printID, workNo, machineOwnedForm.templateType)
        if (isSave) {
            repo?.saveAuditForm(auditForm)
        } else {
            val intent = Intent(this@FormActivity, AuditActivity::class.java)
            intent.putExtra(AuditActivity.EXTRA_AUDIT_FORM, auditForm)
            intent.putExtra(AuditActivity.EXTRA_IS_START_AUDIT, true)
            startActivity(intent)

            finish()
        }
    }

    override fun loadDefineCellValues(formID: String, partNo: String, lineNo: String) {
        progressDialog.setMessage("正在设置规定值...")
        progressDialog.show()
        repo?.getCJCellValues(formID, partNo, lineNo)
                ?.toList()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally {
                    progressDialog.hide()
                    //恢复progress dialog文本
                    progressDialog.setMessage("正在上传...")
                }
                ?.subscribe({cjCell: MutableList<CJCellValue>? ->
                    val frag: CJFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_CJ) as CJFormFragment
                    frag.fillDefineCells(cjCell!!)
                }, {t: Throwable? ->
                    FUtil.showToast(this@FormActivity, "设置规定值失败: " + t?.localizedMessage)
                })
    }
    //----------------------------------------初件表单-------------------------------------

    override fun loadGeneralForm() {
        repo?.getGeneralFromLocal(machineOwnedForm.formID)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                        {generalForm: GeneralForm? ->
                            val frag: GeneralFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_GENERAL) as GeneralFormFragment
                            frag.showFormView(generalForm!!) },
                        {error: Throwable? ->
                            val frag: GeneralFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_GENERAL) as GeneralFormFragment
                            frag.loadFormFailure()}
                )
    }

    override fun loadMaintenanceForm() {
        repo?.getMultiColFromLocal(machineOwnedForm.formID)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                        {multiColForm: MultiColumnForm? ->
                            val frag: MaintenanceFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_MAINTENANCE) as MaintenanceFormFragment
                            frag.showFormView(multiColForm!!)},
                        {error: Throwable? ->
                            val frag: MaintenanceFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_MAINTENANCE) as MaintenanceFormFragment
                            frag.loadFormFailure()}
                )
    }

    override fun loadTemplateFourForm() {
        repo?.getGeneralFromLocal(machineOwnedForm.formID)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                        {generalForm: GeneralForm? ->
                            val frag: TemplateFourFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FOUR) as TemplateFourFormFragment
                            frag.showTemplateFourFormView(generalForm!!) },
                        {error: Throwable? ->
                            val frag: TemplateFourFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FOUR) as TemplateFourFormFragment
                            frag.loadFormFailure()}
                )
    }

    override fun loadTemplateFiveForm() {
        repo?.getGeneralFromLocal(machineOwnedForm.formID)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                        {generalForm: GeneralForm? ->
                            val frag: TemplateFiveFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FIVE) as TemplateFiveFormFragment
                            frag.showTemplateFourFormView(generalForm!!) },
                        {error: Throwable? ->
                            val frag: TemplateFiveFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FIVE) as TemplateFiveFormFragment
                            frag.loadFormFailure()}
                )
    }

    //---------------------------模板四表单点检状态-----------------------------------------
    override fun loadTemplateFourFormCheckStatus() {
        repo?.getFormCheckStatus(machineOwnedForm.refId, machineOwnedForm.formID)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({status: FormCheckStatus? ->
                    val frag: TemplateFourFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FOUR) as TemplateFourFormFragment
                    frag.setFormStatus(status!!)
                }, {t: Throwable? ->
                    Log.e(TAG, "error: " + t?.localizedMessage)
                })
    }

    override fun saveTemplateFourFormCheckStatus(status: FormCheckStatus) {
        repo?.saveFormCheckStatus(status)
    }

    override fun deleteTemplateFourFormCheckStatus(status: FormCheckStatus) {
        repo?.deleteFormStatusRecord(status)
    }
    //---------------------------模板四表单点检状态-----------------------------------------

    //---------------------------模板五-----------------------------------------
    override fun loadTemplateFiveFormCheckStatus() {
        repo?.getFormCheckStatus(machineOwnedForm.refId, machineOwnedForm.formID)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({status: FormCheckStatus? ->
                    val frag: TemplateFiveFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FIVE) as TemplateFiveFormFragment
                    frag.setFormStatus(status!!)
                }, {t: Throwable? ->
                    Log.e(TAG, "error: " + t?.localizedMessage)
                })
    }

    override fun saveTemplateFiveFormCheckStatus(status: FormCheckStatus) {
        repo?.saveFormCheckStatus(status)
    }

    override fun deleteTemplateFiveFormCheckStatus(status: FormCheckStatus) {
        repo?.deleteFormStatusRecord(status)
    }

    override fun addFormRecords(postData: FormPostData) {
        repo?.cacheFormList?.add(postData)
    }

    override fun showFormRecords() {
        val frag: TemplateFiveFormFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FIVE) as TemplateFiveFormFragment
        supportFragmentManager.beginTransaction()
                .add(R.id.frag_container, TemplateFiveItemFragment.newInstance(1), TAG_FRAG_TEMPLATE_FIVE_ITEM)
                .hide(frag)
                .addToBackStack("form_records")
                .commit()
    }

    override fun loadTemplateFiveRecords() {
        val frag: TemplateFiveItemFragment = supportFragmentManager.findFragmentByTag(TAG_FRAG_TEMPLATE_FIVE_ITEM) as TemplateFiveItemFragment
        frag.showRecords(repo?.cacheFormList!!)
    }

    override fun getRecords(): MutableList<FormPostData> {
        return repo?.cacheFormList!!
    }

    //---------------------------模板五-----------------------------------------

    override fun printID(): Long = printDataID

}
