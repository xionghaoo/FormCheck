package xh.formmodules.nfcbound

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.arlib.floatingsearchview.FloatingSearchView
import com.google.gson.Gson
import com.zdtco.BaseActivity
import com.zdtco.FUtil
import com.zdtco.datafetch.data.Machine
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import xh.formmodules.*
import kotlinx.android.synthetic.main.activity_all_machine.*
import xh.formmodules.data.GeneralPostResult
import xh.formmodules.data.NFCTag


class AllMachineActivity : BaseActivity(), MachineItemFragment.OnListFragmentInteractionListener {
    lateinit var repo: AppRepository
    lateinit var frag: MachineItemFragment
    companion object {
        val RESULT_CODE: Int = 1
        val EXTRA_SELECT_RESULT: String = "AllMachineActivity.extra_select_result"
        val TAG_FRAG_MACHINE_LIST: String = "tag_all_machine_list"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_machine)

        title = "設備列表"

        repo = (application as FormApplication).repository as AppRepository

        frag = MachineItemFragment.newInstance(1)

        supportFragmentManager.beginTransaction()
                .add(R.id.frag_container, frag, TAG_FRAG_MACHINE_LIST)
                .commit()

        floating_search_view.setOnQueryChangeListener(object : FloatingSearchView.OnQueryChangeListener {
            override fun onSearchTextChanged(oldQuery: String, newQuery: String) {

                Log.d("test", "old: " + oldQuery + ", new: " + newQuery)
                repo.loadMachinesFormQuery("%" + newQuery + "%")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ machines ->
                            Log.d("test", "machines size = " + machines.size)

                            frag.showMachineList(machines)
                        }, {t: Throwable? ->
                            Log.e("test", "e: " + t?.localizedMessage)
                        })

            }
        })
    }

    override fun loadMachines() {
        progressDialog.show()
        repo.loadAllMachineFromLocal()
                ?.toList()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally { progressDialog.hide() }
                ?.subscribe(
                        {machines: List<Machine> ->
                            frag.showMachineList(machines)
                        },
                        {t: Throwable? ->
                            FUtil.showToast(this, "error: " + t?.localizedMessage)
                        }
                )
    }

    override fun onItemClick(machineID: String) {
        showTagOperation(machineID)
    }

    private fun showTagOperation(machineID: String) {
        AlertDialog.Builder(this)
                .setTitle("选择对标签的操作")
                .setMessage("1、删除，将删除设备对应的NFC标签\n2、绑定，将重新绑定设备对应的NFC标签")
                .setPositiveButton("绑定", {dialog, which ->
                    val intent = Intent()
                    intent.putExtra(EXTRA_SELECT_RESULT, machineID)
                    setResult(RESULT_CODE, intent)
                    finish()
                })
                .setNegativeButton("删除", {dialog, which ->
                    val tag = NFCTag()
                    tag.tags = ArrayList<NFCTag.Tags>()
                    tag.tags.add(NFCTag.Tags(machineID, ""))
                    progressDialog.setMessage("正在删除...")
                    progressDialog.show()
                    repo.boundNFCCard(Gson().toJson(tag))
                            ?.subscribeOn(Schedulers.io())
                            ?.observeOn(AndroidSchedulers.mainThread())
                            ?.doFinally { progressDialog.hide() }
                            ?.subscribe(
                                    {result: GeneralPostResult? ->
                                        if (result?.status.equals("Success")) {
                                            Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show()
                                            repo.boundNFCCardWithMachine(machineID, "")
                                        } else {
                                            Toast.makeText(this, "删除失败: " + result?.message, Toast.LENGTH_SHORT).show()
                                        }
                                    },
                                    {t: Throwable? ->
                                        Toast.makeText(this, "删除失败: " + t?.localizedMessage, Toast.LENGTH_SHORT).show()
                                    })
                })
                .show()
    }
}
