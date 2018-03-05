package com.zdtco.settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.view.View
import android.widget.EditText
import com.xh.formlib.R
import com.zdtco.BaseApplication
import com.zdtco.datafetch.Repository
import kotlinx.android.synthetic.main.activity_stay_time_settings.*

class StayTimeSettingsActivity : AppCompatActivity() {

    private var repo: Repository? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stay_time_settings)

        title = "主页停留时间设定"

        repo = (application as BaseApplication).repository

        stay_time.text = "停留时间: " + repo?.stayTime + "分钟"
        
        stay_time.setOnClickListener { v: View? ->
            val inputTxt = EditText(this)
            inputTxt.inputType = InputType.TYPE_CLASS_NUMBER
            AlertDialog.Builder(this)
                    .setTitle("设定限制时间")
                    .setView(inputTxt)
                    .setPositiveButton("确定", {dialog, which ->
                        var result = inputTxt.text.toString().trim()
                        if (result == "")
                            result = "0"
                        repo?.stayTime = result.toInt()
                        stay_time.text = "停留时间: " + result + "分钟"
                    })
                    .setNegativeButton("取消", {dialog, which ->

                    })
                    .show()
        }
    }
}
