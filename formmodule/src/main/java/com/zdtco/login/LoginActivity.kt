package com.zdtco.login

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.xh.formlib.BuildConfig

import com.xh.formlib.R
import com.zdtco.FUtil
import com.zdtco.home.HomeActivity
import com.zdtco.datafetch.Repository
import com.zdtco.datafetch.data.ContinuousLoginInfo
import com.zdtco.datafetch.data.WorkNoInfo
import com.zdtco.nfc.NFCActivity
import io.reactivex.Observable

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_login.*
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

abstract class LoginActivity : NFCActivity() {

    private val statusBarHeight: Int
        get() {
            var result = 0
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
            return result
        }

    lateinit var progressDialog: ProgressDialog

    private var isCompleteDownloadAllData: Boolean = true

    abstract val repository: Repository

    abstract val reference: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.title = ""
        toolbar.setPadding(0, statusBarHeight, 0, 0)

        progressDialog = FUtil.progressDialog(this, "正在登录...", false)

        ver_code.text = "版本号: " + versionCode()

        app_name_txt.text = appName()

        Glide.with(this)
                .load(getBackgroundImageRes())
                .centerCrop()
                .into(login_background)

    }

    override fun onStart() {
        super.onStart()
        last_update_time.text = "上次资料更新时间: \n" + repository.lastUpdateTime
        continuous_login_days.text = "已连续登录 " + repository.continuousLoginDays + " 天"
    }

    override fun onDestroy() {
        progressDialog.dismiss()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_login, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.pre_download) {
            downloadAllFormData()
            return true
        } else if (item.itemId == R.id.log){
            errorLog()
            return true
        } else {
            return super.onOptionsItemSelected(item)
        }


    }

    override fun receivedDecTag(decTag: String) {
        //收到工卡卡号，进行验证
        Log.d("LoginActivity", "receivedDecTag = " + decTag)

        if (!isCompleteDownloadAllData) {
            FUtil.showToast(this, "数据正在加载中，请等待数据加载完成后刷卡登录")
            return
        }

        if (!FUtil.isNetworkUsable(this)) {
            FUtil.showToast(this, "网络不可用")
        } else {
            jobCardAuthenticate(decTag)
        }
    }

    override fun receivedHexTag(hexTag: String) {

    }

    fun loginAuthorityCheck(workNo: String, name: String) {
        repository.userWorkNo = workNo
        repository.loginAuthCheck(workNo)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .doFinally { progressDialog.dismiss() }
                .subscribe { hasPower ->
                    if (hasPower!!) {
                        updateContinuousLoginInfo(workNo, name)
                    } else {
                        Toast.makeText(reference, "您没有登录权限或本地数据未下载，请下载数据后再试", Toast.LENGTH_SHORT).show()
                    }
                }
    }

    fun startDownloadAllData() {
        isCompleteDownloadAllData = false
    }

    fun downloadDataFailure() {
        isCompleteDownloadAllData = true
        FUtil.showToast(this, "资料下载失败，请重新下载")
    }

    fun updateTimeRecord() {
        Toast.makeText(this, "下载完成，请关闭下载对话框，刷工卡登录", Toast.LENGTH_LONG).show()

        val dateFormat = SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINA)
        val time = dateFormat.format(System.currentTimeMillis())
        last_update_time.text = "上次资料更新时间: \n" + time
        repository.saveLastUpdateTime(time)
        isCompleteDownloadAllData = true
    }

    //6休1检查
    private fun updateContinuousLoginInfo(workNo: String, name: String) {
        repository.getContinuousLoginInfo(workNo)
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe({info: ContinuousLoginInfo? ->
                    if (info == null || info.workNo == "") {
                        repository.saveContinuousLoginInfo(ContinuousLoginInfo(workNo, 1, System.currentTimeMillis()))
                        repository.continuousLoginDays = 1.toString()
                    } else {
                        val interval = (System.currentTimeMillis() - info.date) / 24.0 / 60.0 / 60.0 / 1000.0
                        if (interval > 1 && interval <= 2) {
                            if (info.continuousDays >= 7) {
                                //超过七天，无法登入，状态信息不变
                                FUtil.showToast(this@LoginActivity, "已连续登录7天")
                                return@subscribe
                            } else {
                                //24 - 48小时内连续登入，在不超过七天的情况下，天数加1
                                info.continuousDays += 1
                                info.date = System.currentTimeMillis()
                            }
                        } else if (interval <= 1 && interval > 0) {
                            //24小时内连续登入表单，状态不变
                        } else {
                            //非连续状态，连续天数重置
                            info.date = System.currentTimeMillis()
                            info.continuousDays = 1
                        }
                        repository.saveContinuousLoginInfo(info)
                        repository.continuousLoginDays = info.continuousDays.toString()
                    }
                    //拥有登录权限，登入到主页面
                    login_text.text = workNo
                    startHomePage(workNo, name)
                }, {t: Throwable? ->
                    Log.e("test", "更新6休1信息失败: " + t?.localizedMessage)
                })
    }

    abstract fun startHomePage(workNo: String, name: String)

    abstract fun downloadAllFormData()

    abstract fun jobCardAuthenticate(jobCardCode: String)

    abstract fun versionCode() : String

    abstract fun errorLog()

    abstract fun getBackgroundImageRes() : Int

    abstract fun appName() : String

    fun getRootView() = root_view

}
