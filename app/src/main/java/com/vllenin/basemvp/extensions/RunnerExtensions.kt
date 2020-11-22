package com.vllenin.basemvp.extensions

import com.vllenin.basemvp.XApplication
import kotlinx.coroutines.*
import java.util.*
import kotlin.concurrent.schedule

/**
 * Created by Vllenin on 2020-04-27.
 */

inline fun <T> T.runOnBackgroundWithCoroutine(crossinline block: T.() -> Unit) {
  CoroutineScope(Dispatchers.Default + Job()).launch {
    block()
    this.cancel()
  }
}

inline fun <T> T.runDelayWithTimer(timeDelayMs: Long, crossinline block: T.() -> Unit) {
  Timer().schedule(timeDelayMs) {
    block()
  }
}

inline fun <T> T.runDelayOnMainWithCoroutine(timeDelayMs: Long, crossinline block: T.() -> Unit) {
  CoroutineScope(Dispatchers.Main + Job()).launch {
    delay(timeDelayMs)
    block()
    this.cancel()
  }
}

inline fun <T> T.runDelayOnUIThread(timeDelayMs: Long, crossinline block: T.() -> Unit) {
  XApplication.mainHandler.postDelayed({
    block()
  }, timeDelayMs)
}