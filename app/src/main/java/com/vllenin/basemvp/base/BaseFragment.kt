package com.vllenin.basemvp.base

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import com.vllenin.basemvp.*
import com.vllenin.basemvp.extensions.bouncingAnimation
import com.vllenin.basemvp.extensions.setOnSingleClickListener
import kotlinx.android.synthetic.main.screen_base_action_bar.view.*
import kotlinx.android.synthetic.main.screen_base_header.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel

@ExperimentalCoroutinesApi
abstract class BaseFragment<V : IBaseView, P : BasePresenter<V>> : Fragment(),
    IBaseView {

    abstract var presenter: P?

    protected var coroutineScopeMain: CoroutineScope? = null
    protected var netWorkChangeListener: (isConnection: Boolean) -> Unit = {}
    protected var rotateAnim: Animation? = null
    protected var isShowing = false

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (isStableScreen()) {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            } else {
                activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        }
        rotateAnim = RotateAnimation(0f, 360f,
            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnim?.duration = 1000
        rotateAnim?.interpolator = LinearInterpolator()
        rotateAnim?.repeatMode = Animation.RESTART
        rotateAnim?.repeatCount = Animation.INFINITE
    }

    @CallSuper
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        attachPresenter()

        val baseFragment = inflater.inflate(getLayoutBase(), container, false)
        baseFragment.setOnClickListener {

        }

        if (getLayoutBase() == R.layout.screen_base_action_bar) {
            inflater.inflate(getLayoutResId(), baseFragment.contentFragment, true)
            if (isShowActionBar()) {
                val lp = baseFragment.actionbarContainer.layoutParams as RelativeLayout.LayoutParams
                lp.topMargin =
                    SystemUtils.getStatusBarHeight(context!!)
                baseFragment.actionbarContainer.layoutParams = lp

                baseFragment.actionbarContainer.visibility = View.VISIBLE
                baseFragment.shadowOfActionsBar.visibility = View.VISIBLE
                baseFragment.buttonBackScreen.setOnSingleClickListener {
                    it.bouncingAnimation()
                    activity?.onBackPressed()
                }
            }
        } else if (getLayoutBase() == R.layout.screen_base_header) {
            inflater.inflate(getLayoutResId(), baseFragment.baseContentFragment, true)
            reSetupBaseHeader(baseFragment)
            setupActionsBase(baseFragment)
        }

        return baseFragment
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initActions()
        initData(arguments)
        registerNetworkChangeListener(baseNetworkChangeListener)
    }

    override fun onResume() {
        super.onResume()
        if (getLayoutBase() == R.layout.screen_base_header) {
            activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        isShowing = true
    }

    override fun onPause() {
        super.onPause()
        isShowing = false
    }

    @CallSuper
    override fun onDestroyView() {
        detachPresenter()
        unRegisterNetworkChangeListener(baseNetworkChangeListener)
        super.onDestroyView()
    }

    abstract fun getLayoutResId(): Int

    abstract fun isShowActionBar(): Boolean

    abstract fun initViews()

    abstract fun initActions()

    abstract fun initData(argument: Bundle?)

    override fun getLayoutBase(): Int =
        R.layout.screen_base_action_bar

    override fun displayLogoRiki(show: Boolean) {
        (activity as? BaseActivity<*, *>)?.displayLogoRiki(show)
    }

    override fun displayLoadingView(show: Boolean) {
        (activity as? BaseActivity<*, *>)?.displayLoadingView(show)
    }

    override fun displayBackgroundTrans(show: Boolean, type: String) {
        (activity as? BaseActivity<*, *>)?.displayBackgroundTrans(show, type)
    }

    override fun displayToast(show: Boolean, content: String) {
        (activity as? BaseActivity<*, *>)?.displayToast(show, content)
    }

    override fun displayBottomActionSheet(show: Boolean, itemSelectedCallback: (item: Int) -> Unit) {
        (activity as? BaseActivity<*, *>)?.displayBottomActionSheet(show, itemSelectedCallback)
    }

    override fun getScreenSize(): Size {
        return (activity as? BaseActivity<*, *>)?.getScreenSize() ?: Size(0, 0)
    }

    override fun getHeightSoftKey(): Int {
        return (activity as? BaseActivity<*, *>)?.getHeightSoftKey() ?: 0
    }

    override fun isStableScreen(): Boolean = true

    override fun isLayoutNoLimit() {
        (activity as? BaseActivity<*, *>)?.isLayoutNoLimit()
    }

    override fun backToLoginInterruptBackStack() {
        (activity as? BaseActivity<*, *>)?.backToLoginInterruptBackStack()
    }

    override fun showAlertNotification(content: String, changeColorBackground: Boolean) {
        (activity as? BaseActivity<*, *>)?.showAlertNotification(content, changeColorBackground)
    }

    override fun clearAlertNotification() {
        (activity as? BaseActivity<*, *>)?.clearAlertNotification()
    }

    override fun getToken(): String? = (activity as? BaseActivity<*, *>)?.getToken()

    override fun requestUserInfoChange() {
        (activity as? BaseActivity<*, *>)?.requestUserInfoChange()
    }

    override fun registerNetworkChangeListener(iNetworkChangeListener: INetworkChangeListener) {
        (activity as? BaseActivity<*, *>)?.registerNetworkChangeListener(iNetworkChangeListener)
    }

    override fun unRegisterNetworkChangeListener(iNetworkChangeListener: INetworkChangeListener) {
        (activity as? BaseActivity<*, *>)?.unRegisterNetworkChangeListener(iNetworkChangeListener)
    }

    private fun attachPresenter() {
        presenter?.attachView(this as V)
        val dbAccessProtocols = (activity as? BaseActivity<*, *>)?.getDBAccessProtocols()
        (presenter as? BasePresenter<*>)?.setDBAccessProtocols(dbAccessProtocols)
        coroutineScopeMain = CoroutineScope(Dispatchers.Main)
    }

    private fun detachPresenter() {
        presenter?.detachView()
        coroutineScopeMain?.cancel()
    }

    protected fun setTittleScreen(tittle: String) {
        view?.findViewById<TextView>(R.id.titleScreen)?.text = tittle
    }

    protected fun displayActionBar(show: Boolean) {
        if (show) {
            view?.findViewById<View>(R.id.actionbarContainer)?.visibility = View.VISIBLE
            view?.findViewById<View>(R.id.shadowOfActionsBar)?.visibility = View.VISIBLE
        } else {
            view?.findViewById<View>(R.id.actionbarContainer)?.visibility = View.GONE
            view?.findViewById<View>(R.id.shadowOfActionsBar)?.visibility = View.GONE
        }
    }

    protected fun hideKeyBroad(view: View) {
        val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun reSetupBaseHeader(baseFragment: View) {
        // do something
    }

    private fun setupActionsBase(baseFragment: View) {
        // do something
    }

    private val baseNetworkChangeListener = object :
        INetworkChangeListener {
        override fun networkChanged(isConnection: Boolean) {
            netWorkChangeListener.invoke(isConnection)
        }
    }

}