package com.vllenin.basemvp

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Size

/**
 * Created by Vllenin on 11/22/20.
 */
class XApplication: Application() {
    companion object {
        lateinit var mainHandler: Handler
        var lastClickTime = 0L

        var TOKEN = ""
        var HEIGHT_SOFT_KEY_BROAD = 0
        var SCREEN_SIZE = Size(0, 0)
    }

    override fun onCreate() {
        super.onCreate()
        mainHandler = Handler(Looper.getMainLooper())
    }
}