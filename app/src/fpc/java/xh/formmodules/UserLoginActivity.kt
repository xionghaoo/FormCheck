package xh.formmodules

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
//        var factory: String = intent.getStringExtra(FactorySelectActivity.FACTORY)
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

        startActivityForResult(Intent(this, FactorySelectActivity::class.java), 1)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 1 && resultCode == 1) {
            var factory: String = data!!.getStringExtra(FactorySelectActivity.FACTORY)
            Log.d("test", "hello")
        }
    }

    override fun downloadAllFormData() {
        //点击下载按钮，保存表单资料
        downloadTool.start(this, Arrays.asList(
                Repository.THREAD_MACHINE,
                Repository.THREAD_GENERAL_FORM,
                Repository.THREAD_MULTI_COL_FORM,
                Repository.THREAD_USER,
                Repository.THREAD_FORMULA))
    }

    override fun jobCardAuthenticate(jobCardCode: String) {
        //工卡认证完成后加载表单列表
        progressDialog.show()
        repo.loginAuth(jobCardCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { progressDialog.hide() }
                .subscribe(
                        { workNo: WorkNo? ->
                            //第一步验证，检查工卡有效性
                            if (workNo?.chnname == null || workNo.workno == null) {
                                FUtil.showToast(this, "非法工卡")
                                return@subscribe
                            }
                            Log.d("test", workNo.workno)
                            //第二步验证，检查登录权限
                            loginAuthorityCheck(workNo.workno, workNo.chnname)
                        },
                        { error: Throwable? -> Log.d("test", error?.localizedMessage) }
                )
    }

    override fun onResume() {
        super.onResume()
        UpdateChecker.checkForDialog(this, "http://10.182.34.124:8999/download/app/FpcCheck/android.json")
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
        return R.drawable.login_bg
    }

    override fun appName(): String {
        return "软板设备点检"
    }
}
