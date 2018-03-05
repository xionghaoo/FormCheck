package com.zdtco.audit

import android.app.ProgressDialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.xh.formlib.R
import com.zdtco.BaseApplication
import com.zdtco.FUtil
import com.zdtco.datafetch.Repository
import com.zdtco.datafetch.data.AuditForm
import com.zdtco.datafetch.data.AuditStatus
import com.zdtco.datafetch.data.MachineOwnedForm
import com.zdtco.datafetch.data.WorkNoInfo
import com.zdtco.formui.FormActivity
import com.zdtco.nfc.NFCActivity
import com.zdtco.widgets.AuditView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_audit.*
import java.text.SimpleDateFormat
import java.util.*

class AuditActivity : NFCActivity() {

    companion object {
        val EXTRA_AUDIT_FORM = "AuditActivity.extra_audit_form"
        val EXTRA_IS_START_AUDIT = "AuditActivity.extra_is_start_audit"
    }

    private var repo: Repository? = null
    private lateinit var auditForm: AuditForm
    private val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.CHINA)
    private val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audit)
        title = "表单审核"

        repo = (application as BaseApplication).repository

        auditForm = intent.getParcelableExtra<AuditForm>(EXTRA_AUDIT_FORM)
        val isStartAudit = intent.getBooleanExtra(EXTRA_IS_START_AUDIT, false)

        form_audit_no.text = "表单号: " + auditForm.auditNo
        btn_show_form.setOnClickListener { v: View? ->
            val ownedForm = MachineOwnedForm(auditForm.formID, auditForm.machineID, "", auditForm.formType, 0, -1)
            val intent = Intent(this@AuditActivity, FormActivity::class.java)
            intent.putExtra(FormActivity.EXTRA_FORM, ownedForm)
            intent.putExtra(FormActivity.EXTRA_WORKNO, auditForm.workNo)
            intent.putExtra(FormActivity.EXTRA_IS_PRINT, true)
            intent.putExtra(FormActivity.EXTRA_FORM_PRINT_ID, auditForm.printID)
            startActivity(intent)
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("正在审核...")
        progressDialog.setCancelable(false)

        if (isStartAudit) {
            val list = List<AuditStatus>(7, {index: Int ->
                if (index <= -1) {
                    AuditStatus(index, true, "G1494458", "OK")
                } else {
                    AuditStatus(index, false, "", "")
                }
            })

            audit_view.init(this, list)

            audit_view.onAuditClickListener { v: View? ->
                postJudgement("0")
            }

            audit_view.onRefuseClickListener { v: View? ->
                postJudgement("1")
            }

            audit_view.onLastAuditClick { v: View? ->
                lastAudit()
            }
        } else {
            repo?.getAuditJudgement(auditForm.auditNo)
                    ?.toList()
                    ?.subscribeOn(Schedulers.io())
                    ?.observeOn(AndroidSchedulers.mainThread())
                    ?.subscribe({audits: MutableList<AuditStatus>? ->
                        val size = audits?.size
                        if (size!! < 7) {
                            val emptySize = 7 - size
                            for (i in 0..(emptySize - 1)) {
                                audits.add(AuditStatus(i + size, false, "", ""))
                            }
                            audit_view.init(this@AuditActivity, audits)

                            audit_view.onAuditClickListener { v: View? ->
                                postJudgement("0")
                            }

                            audit_view.onRefuseClickListener { v: View? ->
                                postJudgement("1")
                            }

                            audit_view.onLastAuditClick { v: View? ->
                                lastAudit()
                            }
                        }
                    }, {t: Throwable? ->
                        FUtil.showToast(this@AuditActivity, "加载审核意见失败: " + t?.localizedMessage)
                    })
        }
    }

    override fun onDestroy() {
        progressDialog.dismiss()
        super.onDestroy()
    }

    override fun receivedDecTag(decTag: String?) {
        progressDialog.show()
        repo?.getWorkNoInfo(decTag)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally { progressDialog.hide() }
                ?.subscribe({workNo: WorkNoInfo? ->
                    if (workNo == null) {
                        FUtil.showToast(this@AuditActivity, "非法工卡")
                    } else {
                        audit_view.setActivityViewWorkNo(workNo.workNo)
                    }
                }, {t: Throwable? ->
                    FUtil.showToast(this@AuditActivity, "验证工卡失败: " + t?.localizedMessage)
                })
    }

    override fun receivedHexTag(hexTag: String?) {

    }

    private fun postJudgement(audit: String) {
        if (audit_view.workNo == null || audit_view.workNo == "" || audit_view.workNo == "请扫描工卡") {
            FUtil.showToast(this@AuditActivity, "请先填写工号")
            return
        }
        progressDialog.show()
        repo?.postAuditJudgement(audit_view.workNo,
                auditForm.auditNo,
                (audit_view.activityIndex + 1).toString(),
                dateFormat.format(System.currentTimeMillis()),
                timeFormat.format(System.currentTimeMillis()),
                audit_view.judgement,
                auditForm.formID,
                "1")
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally { progressDialog.hide() }
                ?.subscribe({success: Boolean? ->
                    if (success!!) {
                        //提交审核意见成功
                        audit_view.audit()
                        if (audit == "0") {
                            //审核时才显示下一审核项
                            audit_view.nextAuditShow(object : AuditView.Callback {
                                override fun onAudited() {
                                    postJudgement("0")
                                }

                                override fun onRefuse() {
                                    postJudgement("1")
                                }
                            })
                        } else {
                            repo?.deleteAuditRecord(auditForm)
                            audit_view.lastAudit()
                        }
                    } else {
                        FUtil.showToast(this@AuditActivity, "提交审核意见失败")
                    }
                }, {t: Throwable? ->
                    FUtil.showToast(this@AuditActivity, "提交审核意见失败: " + t?.localizedMessage)
                })
    }

    private fun lastAudit() {
        if (audit_view.workNo == null || audit_view.workNo == "" || audit_view.workNo == "请扫描工卡") {
            FUtil.showToast(this@AuditActivity, "请先填写工号")
            return
        }
        repo?.postAuditJudgement(audit_view.workNo,
                auditForm.auditNo,
                7.toString(),
                dateFormat.format(System.currentTimeMillis()),
                timeFormat.format(System.currentTimeMillis()),
                audit_view.judgement,
                auditForm.formID,
                "0")
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally { progressDialog.hide() }
                ?.subscribe({success: Boolean? ->
                    if (success!!) {
                        //提交审核意见成功
                        audit_view.disableCurrentAudit()
                        audit_view.lastAudit()
                        repo?.deleteAuditRecord(auditForm)
                    } else {
                        FUtil.showToast(this@AuditActivity, "提交审核意见失败")
                    }
                }, {t: Throwable? ->
                    FUtil.showToast(this@AuditActivity, "提交审核意见失败: " + t?.localizedMessage)
                })
    }
}
