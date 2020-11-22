package com.vllenin.basemvp.base

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.text.HtmlCompat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * Created by Vllenin on 7/31/20.
 */
object SystemUtils {
    private const val HEIGHT_STATUS_BAR_NORMAL_MIN = 24
    private const val HEIGHT_STATUS_BAR_NORMAL_MAX = 27

    fun checkDeviceHasNotch(context: Context): Boolean {
        val density = context.resources.displayMetrics.density
        val height =
            getStatusBarHeight(context)
        val heightDp = (height / density).toInt()

        return heightDp !in HEIGHT_STATUS_BAR_NORMAL_MIN..HEIGHT_STATUS_BAR_NORMAL_MAX
    }

    fun getStatusBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        return when (resourceId > 0) {
            true -> context.resources.getDimensionPixelSize(resourceId)
            else -> 0
        }
    }

    fun getNavigationBarHeight(context: Context): Int {
        val resourceId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return when (resourceId > 0) {
            true -> context.resources.getDimensionPixelSize(resourceId)
            else -> 0
        }
    }

    fun getSoftButtonsBarHeight(activity: Activity): Int {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)
        val usableHeight = metrics.heightPixels
        activity.windowManager.defaultDisplay.getRealMetrics(metrics)
        val realHeight = metrics.heightPixels
        return if (realHeight > usableHeight) realHeight - usableHeight else 0
    }

    fun hasSoftKeys(windowManager: WindowManager?): Boolean {
        val display = windowManager?.defaultDisplay
        val realDisplayMetrics = DisplayMetrics()
        display?.getRealMetrics(realDisplayMetrics)
        val realHeight = realDisplayMetrics.heightPixels
        val realWidth = realDisplayMetrics.widthPixels
        val displayMetrics = DisplayMetrics()
        display?.getMetrics(displayMetrics)
        val displayHeight = displayMetrics.heightPixels
        val displayWidth = displayMetrics.widthPixels

        return realWidth - displayWidth > 0 || realHeight - displayHeight > 0
    }

    fun getNavigationBarSize(context: Context): Point? {
        val appUsableSize: Point =
            getAppUsableScreenSize(context)
        val realScreenSize: Point =
            getRealScreenSize(context)

        // navigation bar on the side
        if (appUsableSize.x < realScreenSize.x) {
            return Point(realScreenSize.x - appUsableSize.x, appUsableSize.y)
        }

        // navigation bar at the bottom
        return if (appUsableSize.y < realScreenSize.y) {
            Point(appUsableSize.x, realScreenSize.y - appUsableSize.y)
        } else Point()

        // navigation bar is not present
    }

    fun getAppUsableScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)
        return size
    }

    fun getRealScreenSize(context: Context): Point {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getRealSize(size)
        return size
    }

    fun isNetworkConnection(context: Context): Boolean {
        var result = false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
    }

    fun parseHtml(stringHtml: String?): String {
        var tempAnswer = stringHtml ?: ""
        tempAnswer = tempAnswer.replace("<ruby>", "{")
        tempAnswer = tempAnswer.replace("</ruby>", "}")
        tempAnswer = tempAnswer.replace("<rt>", ";")
        tempAnswer = tempAnswer.replace("</rt>", "")
        tempAnswer = tempAnswer.replace("<rp>(</rp>", "")
        tempAnswer = tempAnswer.replace("<rp>)</rp>", "")
        tempAnswer = tempAnswer.replace("<p>", "")
        tempAnswer = tempAnswer.replace("</p>", "<br/><br/>")
        tempAnswer = tempAnswer.replace("{;}", "")
        if (tempAnswer.length > 10) {
            val regexLatest = tempAnswer.substring(tempAnswer.length - 10)
            if (regexLatest.contentEquals("<br/><br/>")) {
                tempAnswer = tempAnswer.substring(0, tempAnswer.length - 10)
            }
        }
        tempAnswer = HtmlCompat.fromHtml(tempAnswer, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()

        return tempAnswer
    }

    fun convertMillisecondsToHMS(millisecond: Long) : String {
        return String.format(Locale.US, "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millisecond),
            TimeUnit.MILLISECONDS.toMinutes(millisecond) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisecond)),
            TimeUnit.MILLISECONDS.toSeconds(millisecond) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisecond)))
    }

}