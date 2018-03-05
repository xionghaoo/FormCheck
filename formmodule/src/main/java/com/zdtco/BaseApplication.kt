package com.zdtco

import android.app.Application

import com.zdtco.datafetch.Repository

/**
 * Created by G1494458 on 2017/12/23.
 */

abstract class BaseApplication : Application() {
    public var repository: Repository? = null

    override fun onCreate() {
        super.onCreate()
        repository = initRepo()

        val crashHandler = CrashHandler.getInstance()
        crashHandler.init(this)
    }

    protected abstract fun initRepo() : Repository
}
