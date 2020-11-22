package com.vllenin.basemvp.base

import kotlinx.coroutines.*
import java.lang.ref.WeakReference

@ExperimentalCoroutinesApi
abstract class BasePresenter<V : IBaseView> {

    protected lateinit var presenterScope: CoroutineScope

    protected var dbAccessProtocols: IDBAccessProtocols? = null

    private var weakView: WeakReference<V>? = null

    private val isViewAttached: Boolean
        get() = weakView != null && weakView?.get() != null

    protected val view: V?
        get() = weakView?.get()

    fun attachView(view: V) {
        if (!isViewAttached) {
            weakView = WeakReference(view)
        }
        presenterScope = CoroutineScope(Job() + Dispatchers.Default)
    }

    fun setDBAccessProtocols(dbAccessProtocols: IDBAccessProtocols?) {
        this.dbAccessProtocols = dbAccessProtocols
    }

    fun detachView() {
        presenterScope.cancel()
        weakView?.clear()
        weakView = null
    }

    protected fun handleFailureWhenGet(t: Throwable) {
        t.printStackTrace()
        view?.displayLoadingView(false)
        view?.showAlertNotification("SYSTEM_LOAD_DATA_FAILED")
    }

    protected fun handleFailureWhenPost(t: Throwable) {
        t.printStackTrace()
        view?.displayLoadingView(false)
        view?.showAlertNotification("SYSTEM_LOAD_ERROR")
    }

    protected fun parseJSONSafety(runnable: () -> Unit) {
        try {
            runnable.invoke()
        } catch (e: NullPointerException) {
            presenterScope.launch(Job() + Dispatchers.Main) {
                view?.showAlertNotification("SYSTEM_LOAD_DATA_FAILED")
            }
            e.printStackTrace()
        } catch (e: Exception) {
            presenterScope.launch(Job() + Dispatchers.Main) {
                view?.showAlertNotification("SYSTEM_LOAD_DATA_FAILED")
            }
            e.printStackTrace()
        }
    }

    protected fun handleCode(status: Status?, blockForCode200: () -> Unit, blockPlusForCodeError: () -> Unit = {}) {
        when (status?.code) {
            200 -> {
                blockForCode200.invoke()
            }
            401 -> {
                presenterScope.launch(Job() + Dispatchers.Main) {
                    view?.showAlertNotification("LOGIN_TIME_OUT")
                    view?.displayLogoRiki(true)
                    view?.backToLoginInterruptBackStack()
                    blockPlusForCodeError.invoke()
                }
            }
            else -> {
                presenterScope.launch(Job() + Dispatchers.Main) {
                    view?.showAlertNotification("message API")
                    blockPlusForCodeError.invoke()
                }
            }
        }
    }

    data class Status(val code: Int, val message: String)

}