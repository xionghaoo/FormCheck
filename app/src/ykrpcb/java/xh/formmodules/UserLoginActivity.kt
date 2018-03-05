package xh.formmodules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.zdtco.FUtil
import com.zdtco.datafetch.DownloadTool
import com.zdtco.datafetch.Repository
import com.zdtco.login.LoginActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import xh.appupdate.UpdateChecker
import java.util.*

class UserLoginActivity : LoginActivity() {

    lateinit var repo: AppRepository
    lateinit var downloadTool: DownloadTool

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repo = (application as FormApplication).repository as AppRepository
        downloadTool = DownloadTool(repo, object : DownloadTool.Callback {
            override fun downloadComplete() {
                updateTimeRecord()
            }

            override fun downloadStart() {
                startDownloadAllData()
            }

            override fun downloadFailure() {
                downloadDataFailure()
            }
        })

        val btnRegisterUser = Button(this)
        btnRegisterUser.text = "注册人员信息"
        getRootView().addView(btnRegisterUser)

        val viewConstraintTo = getRootView().findViewById<TextView>(R.id.last_update_time)
        val c = ConstraintSet()
        c.connect(btnRegisterUser.id, ConstraintSet.START, viewConstraintTo.id, ConstraintSet.END, 10)
        c.connect(btnRegisterUser.id, ConstraintSet.END, getRootView().id, ConstraintSet.END, 8)
        c.connect(btnRegisterUser.id, ConstraintSet.TOP, viewConstraintTo.id, ConstraintSet.TOP, 8)
        c.connect(btnRegisterUser.id, ConstraintSet.BOTTOM, viewConstraintTo.id, ConstraintSet.BOTTOM, 8)
        c.constrainHeight(btnRegisterUser.id, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        c.constrainWidth(btnRegisterUser.id, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        c.applyTo(getRootView())

        btnRegisterUser.setOnClickListener { v ->
            startActivity(Intent(this@UserLoginActivity, RegisterUserInfoActivity::class.java))
        }
    }

    override fun downloadAllFormData() {
        //点击下载按钮，保存表单资料
        downloadTool.start(this, Arrays.asList(
                Repository.THREAD_USER,
                Repository.THREAD_MACHINE,
                Repository.THREAD_GENERAL_FORM,
                Repository.THREAD_MULTI_COL_FORM))
    }

    override fun jobCardAuthenticate(jobCardCode: String) {
        //工卡认证完成后加载表单列表
        progressDialog.show()
        startHomePage("G1494458", "test")
//        repo.loginAuth(jobCardCode)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doFinally { progressDialog.hide() }
//                .subscribe(
//                        {
//                            workNo: WorkNo? ->
//                            //第一步验证，检查工卡有效性
//                            if (workNo?.chnname == null || workNo.workno == null) {
//                                FUtil.showToast(this, "非法工卡")
//                                return@subscribe
//                            }
//                            Log.d("test", workNo.workno)
//                            //第二步验证，检查登录权限
//                            loginAuthorityCheck(workNo.workno, workNo.chnname)
//                        },
//                        {error: Throwable? -> Log.d("test", error?.localizedMessage) }
//                )
    }

    override fun onResume() {
        super.onResume()
        UpdateChecker.checkForDialog(this, "http://10.52.38.95:8999/download/app/YKRpcbCheck/android.json")
    }

    override fun startHomePage(workNo: String, name: String) {
        val intent = Intent(reference, FormHomeActivity::class.java)
        intent.putExtra(FormHomeActivity.EXTRA_NAME, name)
        intent.putExtra(FormHomeActivity.EXTRA_WORK_NO, workNo)
        startActivity(intent)
        finish()
    }

    override fun errorLog() {
        val intent = Intent(this, LogActivity::class.java)
        startActivity(intent)
    }

    override val repository: Repository
        get() = repo

    override val reference: Context
        get() = this

    override fun versionCode(): String {
        return BuildConfig.VERSION_NAME
    }

    override fun getBackgroundImageRes(): Int {
        return R.drawable.yk_bg_login
    }

    override fun appName(): String {
        return "营口RPCB设备点检"
    }
}