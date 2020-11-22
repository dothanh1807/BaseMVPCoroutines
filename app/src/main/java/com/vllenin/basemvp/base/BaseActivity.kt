package com.vllenin.basemvp.base

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Insets
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Size
import android.view.Gravity
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowMetrics
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.vllenin.basemvp.*
import com.vllenin.basemvp.IBaseView.Companion.VIEW_NONE
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
abstract class BaseActivity<V: IBaseView, P: BasePresenter<V>> : AppCompatActivity(),
    IBaseView {

    abstract var presenter: P?

    private lateinit var networkChangeReceiver: NetworkChangeReceiver
    private lateinit var screenDisplaySize: Size
    private lateinit var toast: Toast
    private var heightSoftKey = 0
    protected var isActivityShowing = false

    private var listNetworkChangeListener = ArrayList<INetworkChangeListener>()

    override fun setTheme(resId: Int) {
        super.setTheme(resId)
        isLayoutNoLimit()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutResId())

        initScreenSize()
        attachPresenter()
        initViews()
        initData()
        initToast()

        networkChangeReceiver = NetworkChangeReceiver()
        registerReceiver(networkChangeReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
    }

    override fun onResume() {
        super.onResume()
        isActivityShowing = true
    }

    override fun onPause() {
        super.onPause()
        isActivityShowing = false
    }

    override fun onDestroy() {
        detachPresenter()
        unregisterReceiver(networkChangeReceiver)
        super.onDestroy()
    }

    abstract fun getLayoutResId(): Int

    abstract fun initViews()

    abstract fun initData()

    override fun getLayoutBase(): Int = VIEW_NONE

    override fun displayToast(show: Boolean, content: String) {
        if (show) {
            if (content.isNotEmpty()) {
                toast.setText(content)
            }
            toast.show()
        } else {
            toast.cancel()
        }
    }

    override fun getScreenSize(): Size = screenDisplaySize

    override fun getHeightSoftKey(): Int = heightSoftKey

    override fun isStableScreen(): Boolean = true

    override fun isLayoutNoLimit() {
        window?.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun getToken(): String? {
        val sharePrefs = getSharedPreferences("RIKI_SHARE_PREFS_NAME", Context.MODE_PRIVATE)
        return sharePrefs.getString("KEY_TOKEN_RIKI_SERVER", null)
    }

    override fun registerNetworkChangeListener(iNetworkChangeListener: INetworkChangeListener) {
        listNetworkChangeListener.add(iNetworkChangeListener)
    }

    override fun unRegisterNetworkChangeListener(iNetworkChangeListener: INetworkChangeListener) {
        listNetworkChangeListener.remove(iNetworkChangeListener)
    }

    fun getDBAccessProtocols(): IDBAccessProtocols? = presenter as? IDBAccessProtocols

    private fun attachPresenter() {
        presenter?.attachView(this as V)
    }

    private fun detachPresenter() {
        presenter?.detachView()
    }

    @SuppressLint("ShowToast")
    private fun initToast() {
        toast = Toast.makeText(this, "-----------------------", Toast.LENGTH_SHORT)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            toast.view?.background?.colorFilter = BlendModeColorFilter(ContextCompat.getColor(this,
                R.color.design_default_color_secondary_variant
            ), BlendMode.SRC_ATOP)
        } else {
            toast.view?.background?.setColorFilter(ContextCompat.getColor(this,
                R.color.design_default_color_secondary_variant
            ), PorterDuff.Mode.SRC_ATOP)
        }
        val toastContentView = toast.view as? LinearLayout

        val textView = toastContentView?.findViewById<TextView>(android.R.id.message)
        textView?.gravity = Gravity.CENTER
    }

    private fun initScreenSize() {
        screenDisplaySize = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics: WindowMetrics = windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets
                .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            Size(windowMetrics.bounds.width(), windowMetrics.bounds.height() - insets.bottom)
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
        XApplication.SCREEN_SIZE = screenDisplaySize
    }

    inner class NetworkChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isConnection =
                SystemUtils.isNetworkConnection(context!!)
            listNetworkChangeListener.forEach {
                it.networkChanged(isConnection)
            }
            if (!isConnection && isActivityShowing) {
                // do somethings
            }
        }
    }

}