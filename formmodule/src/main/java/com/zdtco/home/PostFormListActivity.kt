package com.zdtco.home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ExpandableListView

import com.xh.formlib.R
import com.zdtco.BaseActivity
import com.zdtco.BaseApplication
import com.zdtco.FUtil
import com.zdtco.datafetch.Repository
import com.zdtco.datafetch.data.FormPrintData
import com.zdtco.datafetch.data.MachineOwnedForm
import com.zdtco.formui.FormActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_post_form_list.*
import kotlinx.android.synthetic.main.widget_form_operation.*
import java.util.concurrent.TimeUnit

class PostFormListActivity : BaseActivity() {

    lateinit var adapter: PostFormListAdapter
    private var repo: Repository? = null
    private var uploadCount: Int = 0
    private var mHasPost: Boolean = false
    private var mItems: MutableList<FormPrintData>? = ArrayList<FormPrintData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_form_list)

        repo = (application as BaseApplication).repository

        progressDialog = FUtil.progressDialog(this, "正在上传...", false)

        val postStatus = intent.getIntExtra(EXTRA_POST_STATUS, -1)
        when(postStatus) {
            STATUS_POSTED -> {
                title = "已上传列表"
                mHasPost = true
                submit_button.visibility = View.GONE
            }
            STATUS_UN_POST -> {
                title = "未上传列表"
                mHasPost = false
            }
            else -> {
                throw Exception("must post a status")
            }
        }

        adapter = PostFormListAdapter(mItems)
        form_list.setAdapter(adapter)
        form_list.setOnChildClickListener(ExpandableListView.OnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val item = adapter.getChild(groupPosition, childPosition) as FormPrintData
            val ownedForm = MachineOwnedForm(item.formID, item.machineID, "", item.formType.toInt(), 0, -1)
            val intent = Intent(this@PostFormListActivity, FormActivity::class.java)
            intent.putExtra(FormActivity.EXTRA_FORM, ownedForm)
            intent.putExtra(FormActivity.EXTRA_WORKNO, item.workNo)
            intent.putExtra(FormActivity.EXTRA_IS_PRINT, true)
            intent.putExtra(FormActivity.EXTRA_FORM_PRINT_ID, item.id)
            startActivity(intent)
            true
        })

        submit_button.setOnClickListener { v ->
            val submitItems: MutableList<FormPrintData> = ArrayList<FormPrintData>()
            submitItems.addAll(adapter.checkedItems)
            progressDialog.show()
            uploadCount = submitItems.size
            progressDialog.setMessage("正在上传...\n剩余 " + uploadCount + " 项")
            postCheckedItems(submitItems, 0)
        }

        delete_button.setOnClickListener { v ->
            val deleteItems: MutableList<FormPrintData> = adapter.checkedItems
            for (item in deleteItems) {
                repo?.deleteFormPrintData(item)
                item.hasDeleted = true
            }
            adapter.notifyDataStructureChanged(mItems)
        }

        select_all.setOnClickListener {
            adapter.selectAll()
        }

        release_all.setOnClickListener {
            adapter.releaseAll()
        }

        loadFormPrintDataList(mHasPost)
    }

    private fun loadFormPrintDataList(hasPost: Boolean) {
        repo?.getFormPrintDataList(hasPost)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({list: MutableList<FormPrintData>? ->
                    mItems = list
                    adapter.notifyDataStructureChanged(mItems)
                },{t: Throwable? ->
                    FUtil.showToast(this@PostFormListActivity, "加载表单列表失败：" + t?.localizedMessage)
                })
    }

    private fun postCheckedItems(items: MutableList<FormPrintData>, index: Int) {
        if (index >= items.size) {
            progressDialog.hide()
            return
        }
        repo?.postFormData(items[index].postData)
                ?.delay(500, TimeUnit.MILLISECONDS)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({success: Boolean? ->
                    if (success!!) {
                        //上传成功
                        items[index].hasPost = true
                        items[index].hasDeleted = true
                        repo?.setFormPrintDataPostStatus(items[index])
                                ?.subscribeOn(Schedulers.io())
                                ?.observeOn(AndroidSchedulers.mainThread())
                                ?.subscribe { adapter.notifyDataStructureChanged(items) }

//                        FUtil.showToast(this@PostFormListActivity, items[index].formID + " 上传成功")
                        //传下一笔数据
                        uploadCount --
                        progressDialog.setMessage("正在上传...\n剩余 " + uploadCount + " 项")
                        postCheckedItems(items, index + 1)
                    } else {
                        progressDialog.hide()
                        FUtil.showToast(this@PostFormListActivity, items[index].formID + " 上传失败，请重新上传")
                    }
                }, {t: Throwable? ->
                    progressDialog.hide()
                    FUtil.showToast(this@PostFormListActivity, "上传失败，请重新上传: " + t?.localizedMessage)
                })
    }

    companion object {

        val EXTRA_POST_STATUS = "com.zdtco.SavedFieldListActivity.extra_post_status"

        val STATUS_POSTED = 0
        val STATUS_UN_POST = 1
    }
}
