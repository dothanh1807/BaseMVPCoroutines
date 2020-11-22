package com.vllenin.basemvp.extensions

import android.view.View
import com.vllenin.basemvp.base.OnSingleClickListener
import com.vllenin.basemvp.base.OnSingleClickListener.Companion.MIN_CLICK_INTERVAL_NORMAL

/**
 * Created by Vllenin on 8/7/20.
 */
fun View.setOnSingleClickListener(timeInterval: Int = MIN_CLICK_INTERVAL_NORMAL, callback: (view: View) -> Unit) {
    setOnClickListener(object : OnSingleClickListener(timeInterval) {
        override fun onSingleClick(view: View?) {
            callback.invoke(view!!)
        }
    })
}