package xh.formmodules

import android.content.Intent
import android.os.Bundle
import com.zdtco.FUtil
import com.zdtco.home.HomeActivity
import xh.formmodules.nfcbound.NFCBoundActivity

/**
 * Created by G1494458 on 2018/1/3.
 */

class FormHomeActivity : HomeActivity() {
    private lateinit var workNo: String
    private lateinit var userName: String

    companion object {
        val EXTRA_NAME: String = "HomeActivity.extra_name"
        val EXTRA_WORK_NO: String = "HomeActivity.extra_work_no"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userName = intent.getStringExtra(EXTRA_NAME)
        workNo = intent.getStringExtra(EXTRA_WORK_NO)

        if (FUtil.isApkInDebug(this)) {
            userName = "测试用户"
            workNo = "G1494458"
        }

        tvName.text = userName
        tvWorkNo.text = workNo
    }

    override fun logout() {
        val intent = Intent(this@FormHomeActivity, UserLoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun startNFCBoundPage() {
        startActivity(Intent(this, NFCBoundActivity::class.java))
    }

    override fun workNo(): String {
        return workNo
    }
}
