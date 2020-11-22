package com.vllenin.basemvp

import android.os.SystemClock
import android.view.View

/**
 * Created by Vllenin on 8/7/20.
 */
abstract class OnSingleClickListener(
    private val timeInterval: Int
) : View.OnClickListener {

    companion object {
        const val MIN_CLICK_INTERVAL_SHORT = 300
        const val MIN_CLICK_INTERVAL_NORMAL = 500
        const val MIN_CLICK_INTERVAL_LONG = 1000
    }

    abstract fun onSingleClick(view: View?)

    override fun onClick(v: View?) {
        val currentClickTime = SystemClock.uptimeMillis()
        val elapsedTime = currentClickTime - XApplication.lastClickTime

        if (elapsedTime <= timeInterval) {
            return
        }

        onSingleClick(v)
        XApplication.lastClickTime = currentClickTime
    }

}