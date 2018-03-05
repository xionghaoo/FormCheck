package com.zdtco.audit

import android.app.ListActivity
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.SimpleAdapter
import android.widget.Toolbar
import com.xh.formlib.R
import com.zdtco.BaseApplication
import com.zdtco.FUtil
import com.zdtco.datafetch.Repository
import com.zdtco.datafetch.data.AuditForm
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_audit_form_list.*

class AuditFormListActivity : AppCompatActivity() {

    private var repo: Repository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audit_form_list)

        title = "待审核表单列表"

        repo = (application as BaseApplication).repository

//        val items = List<String>(4, {index: Int ->
//            when(index) {
//                0 -> "One"
//                1 -> "two"
//                2 -> "three"
//                3 -> "four"
//                else -> ""
//            }
//        })
    }

    override fun onResume() {
        super.onResume()
        repo?.loadAuditForms()
                ?.toList()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({items: MutableList<AuditForm>? ->
                    val adapter = AuditAdapter(this@AuditFormListActivity, items!!)
                    audit_list.adapter = adapter
                    audit_list.setOnItemClickListener { parent, view, position, id ->
                        val auditForm = parent.getItemAtPosition(position) as AuditForm
                        val intent = Intent(this@AuditFormListActivity, AuditActivity::class.java)
                        intent.putExtra(AuditActivity.EXTRA_AUDIT_FORM, auditForm)
                        intent.putExtra(AuditActivity.EXTRA_IS_START_AUDIT, false)
                        startActivity(intent)
                    }
                }, {t: Throwable? ->
                    FUtil.showToast(this@AuditFormListActivity, "加载审核表单失败：" + t?.localizedMessage)
                })
    }
}
