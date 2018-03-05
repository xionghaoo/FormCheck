package com.zdtco.settings

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.xh.formlib.R
import kotlinx.android.synthetic.main.activity_form_settings.*

class FormSettingsActivity : AppCompatActivity() {

    companion object {
        private val SETTINGS_TIME_LIMIT = 0
        private val SETTINGS_HOME_STAY_TIME = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form_settings)
        title = "表单设定"

//        startActivity(Intent(this, ItemListActivity::class.java))

        val list = List<String>(2, { index: Int ->
            when(index) {
                SETTINGS_TIME_LIMIT -> "表单时间限制设定"
                SETTINGS_HOME_STAY_TIME -> "主页面停留时间设定"
                else -> ""
            }

        })

        settings_grid_view.adapter = ArrayAdapter(this, R.layout.item_settings_grid, list)

        settings_grid_view.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            when(position) {
                SETTINGS_TIME_LIMIT -> startActivity(Intent(this@FormSettingsActivity, FormTimeLimitSettingsActivity::class.java))
                SETTINGS_HOME_STAY_TIME -> startActivity(Intent(this@FormSettingsActivity, StayTimeSettingsActivity::class.java))
                else -> {}
            }
        }
    }
}
