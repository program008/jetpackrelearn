package com.enabot.mylibrary

import android.app.Activity
import android.app.Application
import androidx.annotation.CallSuper
import androidx.annotation.NonNull
import androidx.lifecycle.*
import java.lang.ref.WeakReference

/**
 * @author zmp
 * @date : 2021/3/5 13:11
 * des:
 */
open class BaseViewModel(@NonNull application: Application) : AndroidViewModel(application),
    DefaultLifecycleObserver {

    private var mLifecycle: WeakReference<Lifecycle>? = null

    fun bindLifecycle(lifecycle: LifecycleOwner) {
        mLifecycle = WeakReference(lifecycle.lifecycle)
        mLifecycle?.get()?.addObserver(this)
    }

    @Volatile
    private var isReleased = false

    @CallSuper
    override fun onPause(owner: LifecycleOwner) {
        if (owner is Activity) {
            if (owner.isFinishing) {
                releaseData()
            }
        }
    }

    @Deprecated("replace onReleaseData")
    override fun onCleared() {
        releaseData()
    }

    /**
     * 释放数据
     */
    private fun releaseData() {
        if (isReleased) {
            return
        }
        isReleased = true
        mLifecycle?.get()?.removeObserver(this)
        onReleaseData()
    }

    /**
     * 释放数据
     * fix onCleared 回调太慢
     */
    open fun onReleaseData() {
    }
}