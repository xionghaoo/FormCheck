package com.zdtco.home

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.xh.formlib.R
import com.zdtco.BaseActivity
import com.zdtco.BaseApplication
import com.zdtco.datafetch.Repository
import com.zdtco.datafetch.data.MachineOwnedForm
import com.zdtco.formui.FormActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class UnboundMachineActivity : BaseActivity(), MachineListFragment.OnMachineListFragListener {

    internal var repo: Repository? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_unbound_machine)
        title = "未绑定设备列表"

        repo = (application as BaseApplication).repository

    }

    override fun onStart() {
        super.onStart()
        val frag = supportFragmentManager.findFragmentByTag("unbound_machine_fragment") as MachineListFragment
        frag.receivedNFCCode("")
    }

    override fun onItemClicked(form: MachineOwnedForm) {
        val intent = Intent(this, FormActivity::class.java)
        intent.putExtra(FormActivity.EXTRA_FORM, form)
        intent.putExtra(FormActivity.EXTRA_WORKNO, repo?.userWorkNo)
        startActivity(intent)
    }

    override fun loadMachines(nfcCode: String) {
        progressDialog.show()
        repo?.getMachineListFromLocal(nfcCode)
                ?.toList()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally { progressDialog.hide() }
                ?.subscribe { items ->
                    val frag: MachineListFragment = supportFragmentManager.findFragmentByTag("unbound_machine_fragment") as MachineListFragment
                    frag.updateMachineList(items)
                }
    }
}
