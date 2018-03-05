package com.zdtco.settings

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.widget.EditText
import com.google.gson.Gson
import com.xh.formlib.R
import com.zdtco.BaseActivity
import com.zdtco.BaseApplication
import com.zdtco.FUtil
import com.zdtco.datafetch.Repository
import com.zdtco.datafetch.data.LimitTime
import com.zdtco.datafetch.data.Line
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class FormTimeLimitSettingsActivity : BaseActivity(), MachineTimeLimitFragment.OnListFragmentInteractionListener {
    private var repo: Repository? = null
    lateinit var frag: MachineTimeLimitFragment

    companion object {
        val TAG_FRAG_TIME_LIMIT = "time_limit_frag"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_time_limit_settings)

        title = "线体时间限制设置"

        repo = (application as BaseApplication).repository

        frag = MachineTimeLimitFragment.newInstance(1)

        supportFragmentManager.beginTransaction()
                .add(R.id.frag_container, frag, TAG_FRAG_TIME_LIMIT)
                .commit()

        progressDialog.setMessage("设定中...")
    }

    override fun loadLinesList() {
        progressDialog.setMessage("加载中...")
        progressDialog.show()
        repo?.loadLines()
                ?.toList()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally {
                    progressDialog.hide()
                    progressDialog.setMessage("设定中...")
                }
                ?.subscribe(
                        {machines: List<Line> ->
                            frag.showLineList(machines)
                        },
                        {t: Throwable? ->
                            FUtil.showToast(this, "error: " + t?.localizedMessage)
                        }
                )
    }

    override fun onItemClicked(machineID: String) {
        val inputTxt = EditText(this)
        inputTxt.inputType = InputType.TYPE_CLASS_NUMBER
        AlertDialog.Builder(this)
                .setTitle("设定限制时间")
                .setView(inputTxt)
                .setPositiveButton("确定", {dialog, which ->
                    var result = inputTxt.text.toString().trim()
                    if (result == "")
                        result = "0"
                    val limitTime = LimitTime()
                    limitTime.lTimeList.add(LimitTime.LTime(machineID, result.toInt()))
                    progressDialog.show()
                    repo?.bindLineLimitTime(Gson().toJson(limitTime))
                            ?.subscribeOn(Schedulers.io())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.doFinally { progressDialog.hide() }
                            ?.subscribe({success: Boolean? ->
                                if (success!!) {
                                    repo?.updateMachineTimeLimit(machineID, result.toLong())
                                    frag.refresh()
                                } else {
                                    FUtil.showToast(this@FormTimeLimitSettingsActivity, "设定失败, 请重新设定")
                                }
                            }, {t: Throwable? ->
                                FUtil.showToast(this@FormTimeLimitSettingsActivity, "设定失败: " + t?.localizedMessage)
                            })

                })
                .setNegativeButton("取消", {dialog, which ->

                })
                .show()
    }
}
