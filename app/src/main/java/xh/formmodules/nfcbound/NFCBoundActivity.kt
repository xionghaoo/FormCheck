package xh.formmodules.nfcbound

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.zdtco.nfc.NFCActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_nfcbound.*
import xh.formmodules.AppRepository
import com.zdtco.FUtil
import xh.formmodules.FormApplication
import xh.formmodules.R
import xh.formmodules.data.GeneralPostResult
import xh.formmodules.data.NFCTag

class NFCBoundActivity : NFCActivity() {

    lateinit var progressDialog: ProgressDialog

    lateinit var repo: AppRepository
    private var nfcCode: String? = null
    private var machineID: String? = null

    companion object {
        val REQUEST_CODE: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nfcbound)

        title = "NFC标签绑定"

        repo = (application as FormApplication).repository as AppRepository

        progressDialog = FUtil.progressDialog(this, "正在綁定...", false)
    }

    override fun onDestroy() {
        progressDialog.dismiss()
        super.onDestroy()
    }

    override fun receivedDecTag(decTag: String?) {
    }

    override fun receivedHexTag(hexTag: String?) {
        nfc_code.text = hexTag
        nfcCode = hexTag
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == AllMachineActivity.RESULT_CODE) {
                val result = data?.getStringExtra(AllMachineActivity.EXTRA_SELECT_RESULT)
                machine_id.text = result
                machineID = result
            }
        }

    }

    fun onClick(view: View) {
        when(view.id) {
            R.id.btn_select_device -> {
                startActivityForResult(Intent(this, AllMachineActivity::class.java), REQUEST_CODE)
            }
            R.id.btn_post -> {
                if (nfcCode == null || machineID == null) {
                    Toast.makeText(this, "NFC编号或设备标号为空！", Toast.LENGTH_SHORT).show()
                    return
                }
                val tag = NFCTag()
                tag.tags = ArrayList<NFCTag.Tags>()
                tag.tags.add(NFCTag.Tags(machineID, nfcCode))
                progressDialog.show()
                repo.boundNFCCard(Gson().toJson(tag))
                        ?.subscribeOn(Schedulers.io())
                        ?.observeOn(AndroidSchedulers.mainThread())
                        ?.doFinally { progressDialog.hide() }
                        ?.subscribe(
                                {result: GeneralPostResult? ->
                                    if (result?.status.equals("Success")) {
                                        Toast.makeText(this, "绑定成功", Toast.LENGTH_SHORT).show()
                                        repo.boundNFCCardWithMachine(machineID, nfcCode)
                                    } else {
                                        Toast.makeText(this, "绑定失败: " + result?.message, Toast.LENGTH_SHORT).show()
                                    }
                                },
                                {t: Throwable? ->
                                    Toast.makeText(this, "绑定失败: " + t?.localizedMessage, Toast.LENGTH_SHORT).show()
                                })
            }
        }
    }
}
