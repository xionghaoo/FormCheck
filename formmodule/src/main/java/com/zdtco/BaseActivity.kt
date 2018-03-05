package com.zdtco

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

/**
 * Created by G1494458 on 2018/1/4.
 */

abstract class BaseActivity : AppCompatActivity() {

    lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        progressDialog = FUtil.progressDialog(this, "加載中...", false)

    }

    override fun onDestroy() {
        progressDialog.dismiss()
        super.onDestroy()
    }
}
