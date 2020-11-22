package com.vllenin.basemvp

import android.util.Size
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Created by Vllenin on 2019-09-02.
 */
@ExperimentalCoroutinesApi
interface IBaseView {

    companion object {
        const val VIEW_NONE = 0
        const val WHITE_TRANS = "white_trans"
        const val BLACK_TRANS = "black_trans"
    }

    fun getLayoutBase(): Int

    fun displayLogoRiki(show: Boolean)

    fun displayLoadingView(show: Boolean)

    fun displayBackgroundTrans(show: Boolean, type: String)

    fun displayToast(show: Boolean, content: String = "")

    fun displayBottomActionSheet(show: Boolean, itemSelectedCallback: (item: Int) -> Unit)

    fun getScreenSize(): Size

    fun getHeightSoftKey(): Int

    fun isStableScreen(): Boolean

    fun isLayoutNoLimit()

    fun backToLoginInterruptBackStack()

    fun showAlertNotification(content: String, changeColorBackground: Boolean = false)

    fun clearAlertNotification()

    fun getToken(): String?

    fun requestUserInfoChange()

    fun registerNetworkChangeListener(iNetworkChangeListener: INetworkChangeListener)

    fun unRegisterNetworkChangeListener(iNetworkChangeListener: INetworkChangeListener)
}