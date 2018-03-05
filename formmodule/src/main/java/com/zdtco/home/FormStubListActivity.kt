package com.zdtco.home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.xh.formlib.R
import com.zdtco.BaseActivity
import com.zdtco.BaseApplication
import com.zdtco.FUtil
import com.zdtco.datafetch.Repository
import com.zdtco.datafetch.data.FormStub
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FormStubListActivity : AppCompatActivity(), FormStubFragment.OnFormStubFragmentListener {

    private var repo: Repository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_stub_list)
        title = "暂存记录列表"
        repo = (application as BaseApplication).repository

        supportFragmentManager.beginTransaction()
                .add(R.id.form_stub_frag, FormStubFragment.newInstance(1))
                .commit()

    }

    override fun loadFormStubList() {
        repo?.loadAllFormStubRecord()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({formStubs: MutableList<FormStub>? ->
                    val frag: FormStubFragment = supportFragmentManager.findFragmentById(R.id.form_stub_frag) as FormStubFragment
                    frag.showFormStubList(formStubs)
                }, {error: Throwable? ->
                    FUtil.showToast(this@FormStubListActivity, "加载暂存记录失败: " + error?.localizedMessage)
                })
    }
}
