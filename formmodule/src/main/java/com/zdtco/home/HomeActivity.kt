package com.zdtco.home

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.bumptech.glide.Glide

import com.xh.formlib.R
import com.zdtco.BaseApplication
import com.zdtco.FUtil
import com.zdtco.audit.AuditFormListActivity
import com.zdtco.datafetch.Repository
import com.zdtco.datafetch.data.Machine
import com.zdtco.datafetch.data.MachineOwnedForm
import com.zdtco.formui.FormActivity
import com.zdtco.nfc.NFCActivity
import com.zdtco.settings.FormSettingsActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_home.*
import java.util.*

abstract class HomeActivity : NFCActivity(),
        MachineListFragment.OnMachineListFragListener {

    internal var repo: Repository? = null
    lateinit var tvName: TextView
    lateinit var tvWorkNo: TextView

    lateinit var progressDialog: ProgressDialog

    private var timer: Timer? = null

    private var timerSeconds: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        title = "设备及其表单列表"

        repo = (application as BaseApplication).repository

//        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
//        fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
//        }

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        val navView = findViewById<View>(R.id.nav_view) as NavigationView
        navView.setNavigationItemSelectedListener { item ->
            // Handle navigation view item clicks here.
            val id = item.itemId

            if (id == R.id.nav_unbound_machine_query) {
                // Handle the camera action
                startActivity(Intent(this@HomeActivity, UnboundMachineActivity::class.java))
            } else if (id == R.id.form_stub_list) {
                startActivity(Intent(this@HomeActivity, FormStubListActivity::class.java))
            } else if (id == R.id.un_post_list) {
                val intent = Intent(this@HomeActivity, PostFormListActivity::class.java)
                intent.putExtra(PostFormListActivity.EXTRA_POST_STATUS, PostFormListActivity.STATUS_UN_POST)
                startActivity(intent)
            } else if (id == R.id.posted_list) {
                val intent = Intent(this@HomeActivity, PostFormListActivity::class.java)
                intent.putExtra(PostFormListActivity.EXTRA_POST_STATUS, PostFormListActivity.STATUS_POSTED)
                startActivity(intent)
            } else if (id == R.id.audit_list) {
                startActivity(Intent(this@HomeActivity, AuditFormListActivity::class.java))
            } else if (id == R.id.nav_nfc_binding) {
                startNFCBoundPage()
            } else if (id == R.id.form_settings) {
                startActivity(Intent(this@HomeActivity, FormSettingsActivity::class.java))
            } else if (id == R.id.nav_logout) {
                logout()
            }

            val drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
            drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }

        tvName = navView.getHeaderView(0).findViewById<TextView>(R.id.name)
        tvWorkNo = navView.getHeaderView(0).findViewById<TextView>(R.id.work_no)

        progressDialog = FUtil.progressDialog(this, "已感应到NFC卡, 正在读取设备资料...", false)
    }

    override fun onStart() {
        super.onStart()
        pre_scan.visibility = View.VISIBLE
        Glide.with(this)
                .load(R.drawable.nfc_tag_scan)
                .into(nfc_tag_scan)
        val frag: MachineListFragment = supportFragmentManager.findFragmentById(R.id.home_fragment) as MachineListFragment
        frag.updateMachineList(Collections.emptyList<Machine>())

        startTimerTask()
    }

    override fun onStop() {
        super.onStop()
        if (timer != null) {
            timer?.cancel()
            timer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        progressDialog.dismiss()
    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.home, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//
//
//        return if (id == R.id.action_settings) {
//            true
//        } else super.onOptionsItemSelected(item)
//
//    }

    abstract fun startNFCBoundPage()

    abstract fun workNo() : String

    abstract fun logout()

    override fun receivedDecTag(decTag: String?) {
    }

    override fun receivedHexTag(hexTag: String?) {
        Log.d("test", hexTag)
        progressDialog.show()
        val frag: MachineListFragment = supportFragmentManager.findFragmentById(R.id.home_fragment) as MachineListFragment
        //d9128c2f前处理四号巡检表、d980cc2d黑影1号线巡检表、470e430b工卡、98f9aa2d滚压机
        frag.receivedNFCCode(hexTag)
    }

    override fun onItemClicked(form: MachineOwnedForm) {
        val intent = Intent(this, FormActivity::class.java)
        intent.putExtra(FormActivity.EXTRA_FORM, form)
        intent.putExtra(FormActivity.EXTRA_WORKNO, workNo())
        startActivity(intent)
    }

    override fun loadMachines(nfcCode: String) {
        repo?.getMachineListFromLocal(nfcCode)
                ?.toList()
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.doFinally { progressDialog.hide() }
                ?.subscribe({ items ->
                    if (items.size == 0) {
                        FUtil.showToast(this, "未找到设备")
                        return@subscribe
                    }

                    pre_scan.visibility = View.GONE

                    val frag: MachineListFragment = supportFragmentManager.findFragmentById(R.id.home_fragment) as MachineListFragment
                    frag.updateMachineList(items)
                }, {t: Throwable? -> FUtil.showToast(this, "加载失败: " + t?.localizedMessage)})
    }

    private fun startTimerTask() {

        if (repo?.stayTime!! <= 0) {
            time_limit_txt.visibility = View.GONE
            return
        }

        time_limit_txt.visibility = View.VISIBLE

        timer = Timer()
        timerSeconds = repo?.stayTime!! * 60   //十分钟
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (timerSeconds <= 0) {
                    timer?.cancel()
                    timer = null
                    this@HomeActivity.finish()
                }
                Log.d("FormViewTask", "time " + timerSeconds)

                runOnUiThread { time_limit_txt.text = "    剩余停留时间: " + timerSeconds + " 秒" }

                timerSeconds -= 1
            }
        }, 0, 1000)
    }
}
